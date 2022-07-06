package com.emmily.runtimedeps;

import com.emmily.runtimedeps.format.DependencyURLFormatter;

/**
 * Represents a Maven dependency.
 */
public class MavenDependency {

  private final String groupId;
  private final String artifactId;
  private final String version;
  private final String filename;
  private final MavenRepository mavenRepository;

  public MavenDependency(String groupId,
                         String artifactId,
                         String version,
                         String filename,
                         MavenRepository mavenRepository) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.filename = filename;
    this.mavenRepository = mavenRepository;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public String getFilename() {
    return filename;
  }

  public MavenRepository getMavenRepository() {
    return mavenRepository;
  }

  public String getUrl(String extension) {
    return mavenRepository.getUrl() + DependencyURLFormatter.format(
      groupId,
      artifactId,
      version
    ) + filename + extension;
  }

  public String getPomUrl() {
    return getUrl(".pom");
  }

  public String getJarUrl() {
    return getUrl(".jar");
  }

  @Override
  public String toString() {
    return "MavenDependency{" +
      "groupId='" + groupId + '\'' +
      ", artifactId='" + artifactId + '\'' +
      ", version='" + version + '\'' +
      ", filename='" + filename + '\'' +
      ", mavenRepository=" + mavenRepository +
      '}';
  }
}
