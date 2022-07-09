package com.emmily.runtimedeps.download;

import com.emmily.runtimedeps.auth.Authorizer;
import com.emmily.runtimedeps.channel.DelegatingReadableByteChannel;
import com.emmily.runtimedeps.connection.HttpConnectionBuilder;
import com.emmily.runtimedeps.dependency.FileType;
import com.emmily.runtimedeps.dependency.MavenDependency;
import com.emmily.runtimedeps.exception.RepositoryOfflineException;
import com.emmily.runtimedeps.exception.UnauthorizedClientException;
import com.emmily.runtimedeps.format.UrlFormatter;
import com.emmily.runtimedeps.repository.MavenRepository;
import com.emmily.runtimedeps.repository.RepositoryUtils;
import com.emmily.runtimedeps.resolver.dependency.DependencyResolver;
import com.emmily.runtimedeps.resolver.dependency.ElementDependencyResolver;
import com.emmily.runtimedeps.resolver.dependency.SubDependenciesResolver;
import com.emmily.runtimedeps.xml.XmlHelper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class DefaultDependencyDownloader implements DependencyDownloader {

  private final File destinationFolder;
  private final Logger logger;
  private final Authorizer authorizer;
  private DependencyResolver<Element> subDependenciesResolver;

  public DefaultDependencyDownloader(File destinationFolder,
                                     Logger logger,
                                     Authorizer authorizer,
                                     DependencyResolver<Element> subDependenciesResolver) {
    this.destinationFolder = destinationFolder;
    this.logger = logger;
    this.authorizer = authorizer;
    this.subDependenciesResolver = subDependenciesResolver;

    if (!destinationFolder.exists() && !destinationFolder.mkdir()) {
      logger.severe("Dependencies: Unable to create the destination folder.");
    }
  }

  public DefaultDependencyDownloader(File destinationFolder,
                                     Logger logger,
                                     Authorizer authorizer,
                                     Collection<String> exclusions,
                                     List<MavenRepository> knownRepositories) {
    this(
      destinationFolder,
      logger,
      authorizer,
      null
    );
    setSubDependenciesResolver(new SubDependenciesResolver(
      new ElementDependencyResolver(
        exclusions,
        logger,
        knownRepositories,
        authorizer,
        this
      ),
      this,
      logger
    ));
  }

  @Override
  public File downloadFile(MavenDependency mavenDependency,
                           FileType fileType) throws IOException {
    File file = new File(destinationFolder, String.format(
      fileType.getName(),
      mavenDependency.getArtifactId(),
      mavenDependency.getVersion()
    ));

    if (file.exists()) {
      return file;
    }

    long start = System.currentTimeMillis();

    logger.info(String.format(
      "Dependencies: Downloading the %s file of the dependency %s...",
      fileType,
      mavenDependency.getArtifactId()
    ));

    MavenRepository repository = mavenDependency.getRepository();
    String authorizationHeader = null;

    if (!RepositoryUtils.healthCheck(repository)) {
      throw new RepositoryOfflineException(repository);
    }

    if (RepositoryUtils.requiresAuth(repository)) {
      if (!authorizer.isKnown(repository)) {
        throw new UnauthorizedClientException(repository);
      }

      authorizationHeader = authorizer.getCredentials(repository);
    }

    HttpURLConnection connection = HttpConnectionBuilder
      .builder()
      .url(UrlFormatter.format(mavenDependency, fileType))
      .addProperty("User-Agent", "runtime-deps")
      .addProperty("Authorization", authorizationHeader)
      .build();
    int incomingFileLength = connection.getContentLength();

    /*
    File file = new File(destinationFolder, String.format(
      fileType.getName(),
      mavenDependency.getArtifactId(),
      mavenDependency.getVersion()
    ));

     */
    /*
    if (file.exists()) {
      if (Files.size(Paths.get(file.getPath())) < incomingFileLength) {
        logger.info(String.format(
          "Dependencies: The %s file of the dependency %s was found locally, but it's smaller than the incoming one. " +
            "Re-downloading...",
          fileType,
          mavenDependency.getArtifactId()
        ));

        file.delete();
      } else {
        return file;
      }
    }


     */
    try (
      InputStream inputStream = connection.getInputStream();
      ReadableByteChannel channel = new DelegatingReadableByteChannel(
        Channels.newChannel(inputStream),
        mavenDependency,
        fileType,
        logger,
        incomingFileLength
      );
      FileOutputStream outputStream = new FileOutputStream(file)
    ) {
      outputStream.getChannel().transferFrom(channel, 0, incomingFileLength);
    }

    logger.info(String.format(
      "Dependencies: Successfully downloaded the %s file of the dependency %s (%s ms)",
      fileType,
      mavenDependency.getArtifactId(),
      System.currentTimeMillis() - start
    ));

    return file;
  }

  @Override
  public Collection<File> downloadFiles(MavenDependency mavenDependency,
                                        FileType... fileTypes) throws IOException {
    Collection<File> files = new ArrayList<>();

    for (FileType fileType : fileTypes) {
      files.add(downloadFile(mavenDependency, fileType));
    }

    return files;
  }

  @Override
  public Collection<File> downloadAll(MavenDependency mavenDependency) throws IOException, SAXException {
    Collection<MavenDependency> dependencyTree = subDependenciesResolver.resolve(
      XmlHelper.getElement(downloadFile(mavenDependency, FileType.POM))
    );
    Collection<File> dependencies = new ArrayList<>();

    for (MavenDependency subDependency : dependencyTree) {
      dependencies.add(downloadFile(subDependency, FileType.JAR));
    }

    return dependencies;
  }

  public void setSubDependenciesResolver(DependencyResolver<Element> subDependenciesResolver) {
    this.subDependenciesResolver = subDependenciesResolver;
  }
}
