package com.emmily.runtimedeps.download;

import com.emmily.runtimedeps.FileType;
import com.emmily.runtimedeps.MavenDependency;
import com.emmily.runtimedeps.auth.Authorizer;
import com.emmily.runtimedeps.connection.HttpConnectionBuilder;
import com.emmily.runtimedeps.exception.UnauthorizedClientException;
import com.emmily.runtimedeps.util.RepositoryUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.logging.Logger;

public class DefaultDependencyDownloader implements DependencyDownloader {

  private static final MavenXpp3Reader POM_READER = new MavenXpp3Reader();

  private final File folder;
  private final Logger logger;
  private final Authorizer authorizer;

  public DefaultDependencyDownloader(File folder,
                                     Logger logger,
                                     Authorizer authorizer) {
    this.folder = folder;
    this.logger = logger;
    this.authorizer = authorizer;
  }

  @Override
  public Collection<File> downloadAll(MavenDependency dependency) throws IOException, SAXException {
    return null;
  }

  @Override
  public File downloadFile(MavenDependency dependency,
                           FileType fileType) throws IOException {
    String url = null;

    switch (fileType) {
      case POM: {
        url = dependency.getPomUrl();

        break;
      }
      case JAR: {
        url = dependency.getJarUrl();

        break;
      }
      case MAVEN_METADATA: {
        url = dependency.getMavenMetadataUrl();

        break;
      }
    }

    String authorizationHeader = null;
    Repository repository = dependency.getRepository();

    if (RepositoryUtils.requiresAuth(repository)) {
      if (!authorizer.isKnown(repository)) {
        throw new UnauthorizedClientException(repository);
      }

      authorizationHeader = authorizer.getCredentials(repository);
    }

    HttpURLConnection connection = HttpConnectionBuilder
      .builder()
      .url(url)
      .addProperty("User-Agent", "runtime-deps")
      .addProperty("Authorization", authorizationHeader)
      .build();
    int responseCode = connection.getResponseCode();

    if (responseCode == 404) {
      throw new FileNotFoundException(String.format(
        "Dependencies: The %s file of the dependency %s couldn't be found in the repository %s",
        fileType,
        dependency.getArtifactId(),
        repository.getId()
      ));
    }
    return null;
  }

  @Override
  public Collection<Dependency> getSubDependencies(MavenDependency dependency) throws IOException, XMLStreamException, SAXException {
    return null;
  }

  @Override
  public Collection<Repository> getRepositories(MavenDependency dependency) throws IOException, SAXException {
    return null;
  }
}
