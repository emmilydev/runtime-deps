package com.emmily.runtimedeps.format;

public interface DependencyURLFormatter {

  // Format followed by a dependency: %groupId%/%artifactId%/%version%
  String URL_FORMAT = "%s/%s/%s%s";

  static String format(String groupId,
                       String artifactId,
                       String version) {
    return String.format(
      URL_FORMAT,
      groupId.replace(".", "/"),
      ((artifactId.contains(".")) ? artifactId : artifactId.replace(".", "/")),
      version,
      version.equals("") ? "" : "/"
    );
  }

}
