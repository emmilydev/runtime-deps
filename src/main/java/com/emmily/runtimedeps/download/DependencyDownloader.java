package com.emmily.runtimedeps.download;

import com.emmily.runtimedeps.MavenDependency;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * This class has the responsibility of downloading
 * the files of any {@link MavenDependency}. The default
 * implementation, {@link BasicSchemeDependencyDownloader},
 * downloads files from auth-protected repositories using
 * the BASIC auth schema.
 */
public interface DependencyDownloader {

  /**
   * Downloads the <b>pom.xml</b> file of the given
   * {@code dependency}.
   *
   * @param dependency The target dependency.
   * @return The <b>pom.xml</b> file of the given
   * {@code dependency}.
   */
  File downloadPom(MavenDependency dependency) throws IOException;

  /**
   * Downloads the <b>JAR</b> file of the given
   * {@code dependency}.
   *
   * @param dependency The target dependency.
   * @return The <b>JAR</b> file of the given
   * {@code dependency}.
   */
  File downloadJar(MavenDependency dependency) throws IOException;

  /**
   * Downloads the <b>pom.xml</b> and the <b>JAR</b> file
   * of the given {@code dependency}, as well as its
   * sub-dependencies, which are taken from the <b>pom.xml</b>
   * file.
   *
   * @param dependency The target dependency.
   * @return The <b>pom.xml</b> and the <b>JAR</b> file
   * of the given {@code dependency}, as well as its
   * sub-dependencies, which are taken from the <b>pom.xml</b>
   * file.
   */
  List<File> downloadSubDependencies(MavenDependency dependency) throws IOException, SAXException, ParserConfigurationException;

}
