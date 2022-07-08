package com.emmily.runtimedeps;

public enum FileType {

  JAR("%s.jar"),
  POM("%s.pom"),
  MAVEN_METADATA("maven-metadata.xml");

  private final String name;

  FileType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
