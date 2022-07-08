package com.emmily.runtimedeps.download;

import com.emmily.runtimedeps.FileType;
import com.emmily.runtimedeps.MavenDependency;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface DependencyDownloader {

  Collection<File> downloadAll(MavenDependency dependency) throws IOException, SAXException;

  File downloadFile(MavenDependency dependency,
                    FileType fileType) throws IOException;

  default File downloadPom(MavenDependency dependency) throws IOException {
    return downloadFile(dependency, FileType.POM);
  }

  default File downloadJar(MavenDependency dependency) throws IOException {
    return downloadFile(dependency, FileType.JAR);
  }

  default File downloadMavenMetadata(MavenDependency dependency) throws IOException {
    return downloadFile(dependency, FileType.MAVEN_METADATA);
  }

  Collection<Dependency> getSubDependencies(MavenDependency dependency) throws IOException, XMLStreamException, SAXException;

  Collection<Repository> getRepositories(MavenDependency dependency) throws IOException, SAXException;

}
