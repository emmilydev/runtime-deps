package com.emmily.runtimedeps.dependency;

public enum FileType {

  JAR("%s-%s.jar"),
  POM("%s-%s.pom"),
  MAVEN_METADATA("maven-metadata.xml");

  private final String name;

  FileType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
