package com.emmily.runtimedeps.download;

import com.emmily.runtimedeps.MavenDependency;
import com.emmily.runtimedeps.MavenRepository;
import com.emmily.runtimedeps.connection.HTTPConnectionFactory;
import com.emmily.runtimedeps.download.version.DependencyVersionResolver;
import com.emmily.runtimedeps.format.DependencyURLFormatter;
import com.emmily.runtimedeps.xml.XMLDocumentProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasicSchemeDependencyDownloader implements DependencyDownloader {

  private final String userAgent;
  private final Map<String, String> repositoryCredentials;
  private final File destinationFolder;

  public BasicSchemeDependencyDownloader(String userAgent,
                                         Map<String, String> repositoryCredentials,
                                         File destinationFolder) {
    this.userAgent = userAgent;
    this.repositoryCredentials = repositoryCredentials;
    this.destinationFolder = destinationFolder;

    if (!destinationFolder.exists()) {
      destinationFolder.mkdir();
    }
  }

  @Override
  public File downloadPom(MavenDependency dependency) throws IOException {
    return downloadFile(dependency, "pom");
  }

  @Override
  public File downloadJar(MavenDependency dependency) throws IOException {
    return downloadFile(dependency, "jar");
  }

  @Override
  public List<File> downloadSubDependencies(MavenDependency dependency) throws IOException, SAXException, ParserConfigurationException {
    List<File> result = new ArrayList<>();
    File pomFile = downloadPom(dependency);
    result.add(downloadJar(dependency));

    Document xmlDocument = XMLDocumentProvider.getDocument(pomFile);

    //#region scan-repositories
    List<MavenRepository> repositories = new ArrayList<>();
    repositories.add(MavenRepository.MAVEN_CENTRAL);

    NodeList repositoriesNode = xmlDocument.getElementsByTagName("repository");

    for (int i = 0; i < repositoriesNode.getLength(); i++) {
      Node node = repositoriesNode.item(i);

      if (node.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      Element repositoryElement = (Element) node;

      repositories.add(new MavenRepository(
        repositoryElement.getElementsByTagName("id").item(0).getTextContent(),
        repositoryElement.getElementsByTagName("url").item(0).getTextContent()
      ));
    }
    //#endregion

    //#region scan-dependencies
    List<MavenDependency> dependencies = new ArrayList<>();
    NodeList dependenciesNode = xmlDocument.getElementsByTagName("dependency");

    for (int i = 0; i < dependenciesNode.getLength(); i++) {
      Node node = dependenciesNode.item(i);

      // Check if the current node is an actual dependency.
      if (node.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      Element dependencyElement = (Element) node;

      NodeList scopeNode = dependencyElement.getElementsByTagName("scope");

      if (scopeNode.getLength() != 0 && !scopeNode.item(0).getTextContent().equals("compile")) {
        continue;
      }

      String groupId = dependencyElement.getElementsByTagName("groupId").item(0).getTextContent();
      String artifactId = dependencyElement.getElementsByTagName("artifactId").item(0).getTextContent();
      String version = null;
      NodeList versionNode = dependencyElement.getElementsByTagName("version");

      if (versionNode.getLength() != 0) {
        version = versionNode.item(0).getTextContent();
      }

      MavenDependency subDependency = null;

      for (MavenRepository repository : repositories) {
        if (version == null) {
          version = DependencyVersionResolver.getLatestVersion(
            createConnection(repository.getUrl() + DependencyURLFormatter.format(
              groupId,
              artifactId,
              "",
              "maven-metadata.xml"
            ), repository)
          );
        }

        if (isFilePresent(
          groupId,
          artifactId,
          version,
          repository
        )) {
          subDependency = new MavenDependency(
            groupId,
            artifactId,
            version,
            artifactId + "-" + version,
            repository
          );
          dependencies.add(subDependency);

          break;
        }
      }

      if (subDependency == null) {
        throw new FileNotFoundException(String.format(
          "Dependencies: The sub-dependency %s of the dependency %s couldn't be found. \nGroupId: %s" +
            "\nArtifactId: %s" +
            "\nVersion: %s",
          artifactId,
          dependency.getArtifactId(),
          groupId,
          artifactId,
          version
        ));
      }
    }

    for (MavenDependency subDependency : dependencies) {
      result.addAll(downloadSubDependencies(subDependency));
    }
    //#endregion

    return result;
  }


  private boolean isFilePresent(String groupId,
                                String artifactId,
                                String version,
                                MavenRepository repository) throws IOException {
    HttpURLConnection connection = createConnection(repository.getUrl() + DependencyURLFormatter.format(
      groupId,
      artifactId,
      version
    ), repository);

    return connection.getResponseCode() != 404;
  }

  private File downloadFile(MavenDependency dependency,
                            String extension) throws IOException {
    MavenRepository repository = dependency.getMavenRepository();
    String url = extension.equals("pom") ? dependency.getPomUrl() : dependency.getJarUrl();

    HttpURLConnection connection = createConnection(url, repository);

    File file = new File(destinationFolder, dependency.getFilename() + "." + extension);

    if (file.exists()) {
      file.delete();
    }

    file.createNewFile();

    try (
      InputStream inputStream = connection.getInputStream();
      ReadableByteChannel channel = Channels.newChannel(inputStream);
      FileOutputStream fileOutputStream = new FileOutputStream(file)
    ) {
      fileOutputStream.getChannel().transferFrom(channel, 0, connection.getContentLength());
    }

    return file;
  }

  private HttpURLConnection createConnection(String url,
                                             MavenRepository repository) throws IOException {
    HttpURLConnection connection;

    if (repository.requiresAuth()) {
      if (!repositoryCredentials.containsKey(repository.getId())) {
        throw new IllegalArgumentException("Dependencies: The Maven repository " + repository.getId() + " " +
          "requires authorization credentials, which are missing.");
      }

      String[] usernameAndPassword = repositoryCredentials.get(repository.getId()).split(":");
      connection = HTTPConnectionFactory.createConnection(
        url,
        userAgent,
        usernameAndPassword[0],
        usernameAndPassword[1]
      );
    } else {
      connection = HTTPConnectionFactory.createConnection(
        url,
        userAgent
      );
    }

    return connection;
  }

}
