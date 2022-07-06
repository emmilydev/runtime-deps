package com.emmily.runtimedeps.test;

import com.emmily.runtimedeps.MavenDependency;
import com.emmily.runtimedeps.MavenRepository;
import com.emmily.runtimedeps.download.BasicSchemeDependencyDownloader;
import com.emmily.runtimedeps.download.DependencyDownloader;
import com.emmily.runtimedeps.load.DependencyLoader;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

public class DependencyDownloadTest {

  @Test
  public void runTest() throws ReflectiveOperationException, IOException, ParserConfigurationException, SAXException {
    DependencyDownloader dependencyDownloader = new BasicSchemeDependencyDownloader(
      "runtime-deps",
      Collections.emptyMap(),
      new File(System.getProperty("user.home") + "/Desktop/deps/")
    );

    ClassLoader classLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
    DependencyLoader dependencyLoader = new DependencyLoader(classLoader);

    MavenDependency guavaDependency = new MavenDependency(
      "com.google.guava",
      "guava",
      "31.1-jre",
      "guava-31.1-jre",
      MavenRepository.MAVEN_CENTRAL
    );

    dependencyLoader.loadDependencies(dependencyDownloader.downloadSubDependencies(guavaDependency));
    System.out.println(Class.forName("com.google.common.collect.Multimap", true, classLoader));
  }
}
