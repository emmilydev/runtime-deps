package com.emmily.runtimedeps.dependency;

import com.emmily.runtimedeps.repository.MavenRepository;

import java.beans.ConstructorProperties;

public class MavenDependency {

  private String groupId;
  private String artifactId;
  private String version;
  private MavenRepository mavenRepository;
  private MavenDependency parent;

  @ConstructorProperties({
    "groupId",
    "artifactId",
    "version",
    "repository",
    "parent"
  })
  public MavenDependency(String groupId,
                         String artifactId,
                         String version,
                         MavenRepository mavenRepository,
                         MavenDependency parent) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.mavenRepository = mavenRepository;
    this.parent = parent;
  }


  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public MavenRepository getRepository() {
    return mavenRepository;
  }

  public void setRepository(MavenRepository mavenRepository) {
    this.mavenRepository = mavenRepository;
  }

  public MavenDependency getParent() {
    return parent;
  }

  public void setParent(MavenDependency parent) {
    this.parent = parent;
  }

  @Override
  public String toString() {
    return "MavenDependency{" +
      "groupId='" + groupId + '\'' +
      ", artifactId='" + artifactId + '\'' +
      ", version='" + version + '\'' +
      ", mavenRepository=" + mavenRepository +
      ", parent=" + parent +
      '}';
  }
}
