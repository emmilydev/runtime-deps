package com.emmily.runtimedeps;

import com.emmily.runtimedeps.format.DependencyUrlFormatter;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;

public class MavenDependency extends Dependency {

  private Repository repository;

  public MavenDependency(Repository repository) {
    this.repository = repository;
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  private String getUrl(FileType fileType) {
    if (fileType == FileType.MAVEN_METADATA) {
      return DependencyUrlFormatter.format(
        repository.getUrl(),
        getGroupId(),
        getArtifactId(),
        getVersion(),
        "maven-metadata.xml"
      );
    }

    return DependencyUrlFormatter.format(
      repository.getUrl(),
      getGroupId(),
      getArtifactId(),
      getVersion(),
      String.format(fileType.getName(), getArtifactId() + "-" + getVersion())
    );
  }

  public String getJarUrl() {
    return getUrl(FileType.JAR);
  }

  public String getPomUrl() {
    return getUrl(FileType.POM);
  }

  public String getMavenMetadataUrl() {
    return getUrl(FileType.MAVEN_METADATA);
  }

}
