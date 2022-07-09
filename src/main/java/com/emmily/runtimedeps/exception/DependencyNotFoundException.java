package com.emmily.runtimedeps.exception;

import com.emmily.runtimedeps.dependency.FileType;

public class DependencyNotFoundException extends RuntimeException {

  private static final String MESSAGE = "The %s file of the dependency %s couldn't be found in the known " +
    "repositories.";

  public DependencyNotFoundException(String artifactId,
                                     FileType fileType) {
    super(String.format(
      MESSAGE,
      fileType,
      artifactId
    ));
  }

}
