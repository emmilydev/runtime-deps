package com.emmily.runtimedeps.format;

import com.emmily.runtimedeps.dependency.FileType;
import com.emmily.runtimedeps.dependency.MavenDependency;

public interface UrlFormatter {

  // %groupId%/%artifactId%/%file%
  String DEPENDENCY = "%s/%s/%s";
  // %groupId%/%artifactId%/%version%/%file%
  String VERSIONED_DEPENDENCY = DEPENDENCY + "/%s";

  static String format(MavenDependency dependency,
                       FileType fileType) {
    if (fileType == FileType.MAVEN_METADATA) {
      return dependency.getRepository().getUrl() + String.format(
        DEPENDENCY,
        normalize(dependency.getGroupId()),
        normalize(dependency.getArtifactId(), true),
        "maven-metadata.xml"
      );
    }

    return dependency.getRepository().getUrl() + String.format(
      VERSIONED_DEPENDENCY,
      normalize(dependency.getGroupId()),
      normalize(dependency.getArtifactId(), true),
      dependency.getVersion(),
      String.format(fileType.getName(), dependency.getArtifactId(), dependency.getVersion())
    );
  }

  static String normalize(String string,
                          boolean artifact) {
    if (artifact && string.contains(".")) {
      return string;
    }

    return string.replace(".", "/");
  }

  static String normalize(String string) {
    return normalize(string, false);
  }

}
