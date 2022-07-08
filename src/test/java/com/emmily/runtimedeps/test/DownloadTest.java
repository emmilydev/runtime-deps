package com.emmily.runtimedeps.test;

import com.emmily.runtimedeps.auth.BasicAuthorizer;
import com.emmily.runtimedeps.download.DependencyDownloader;
import com.emmily.runtimedeps.dump.DefaultDependencyDumper;
import com.emmily.runtimedeps.dump.DependencyDumper;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

public class DownloadTest {

  public static void main(String[] args) throws Exception {
    DependencyDownloader dependencyDownloader = new DependencyDownloaderImpl(
      new File(System.getProperty("user.home") + "/Documents/deps/"),
      Logger.getLogger("RuntimeDeps"),
      new BasicAuthorizer(),
      new HashSet<>()
    );

    ClassLoader classLoader = new URLClassLoader(new URL[0], DownloadTest.class.getClassLoader());
    DependencyDumper dependencyDumper = new DefaultDependencyDumper(classLoader);

    Dependency guavaDependency = new Dependency(
      "com.google.guava",
      "guava",
      "31.1-jre",
      Repository.CENTRAL
    );
    Dependency mongoDriverDependency = new Dependency(
      "org.mongodb",
      "mongodb-driver-sync",
      "4.6.1",
      Repository.CENTRAL
    );

    downloadDependencies(dependencyDownloader, mongoDriverDependency);
  }

  public static Collection<File> downloadDependencies(DependencyDownloader dependencyDownloader,
                                                      MavenDependency) throws IOException, XMLStreamException, SAXException {
    Collection<File> files = new HashSet<>();
    files.add(dependencyDownloader.downloadJar(dependency));

    for (Dependency subDependency : dependencyDownloader.getSubDependencies(dependency)) {
      files.addAll(downloadDependencies(dependencyDownloader, subDependency));
    }

    return files;
  }
}
