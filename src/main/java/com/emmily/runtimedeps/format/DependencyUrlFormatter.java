package com.emmily.runtimedeps.format;

public interface DependencyUrlFormatter {

  String URL_FORMAT = "%s%s/%s/%s%s";
  String FILE_FORMAT = URL_FORMAT + "%s";

  static String format(String repository,
                       String groupId,
                       String artifactId,
                       String version) {
    return String.format(
      URL_FORMAT,
      repository,
      groupId.replace(".", "/"),
      ((artifactId.contains(".")) ? artifactId : artifactId.replace(".", "/")),
      version,
      version.equals("") ? "" : "/"
    );
  }

  static String format(MavenDependency) {
    return format(
      dependency.getRepository().getUrl(),
      dependency.getGroupId(),
      dependency.getArtifactId(),
      dependency.getVersion()
    );
  }

  static String format(String repository,
                       String groupId,
                       String artifactId,
                       String version,
                       String file) {
    return String.format(
      FILE_FORMAT,
      repository,
      groupId.replace(".", "/"),
      ((artifactId.contains(".")) ? artifactId : artifactId.replace(".", "/")),
      version,
      version.equals("") ? "" : "/",
      file
    );
  }

  static String format(MavenDependency,
                       String file,
                       boolean includeVersion) {
    return format(
      dependency.getRepository().getUrl(),
      dependency.getGroupId(),
      dependency.getArtifactId(),
      includeVersion ? dependency.getVersion() : "",
      file
    );
  }

  static String format(MavenDependency,
                       String file) {
    return format(dependency, file, true);
  }

}
