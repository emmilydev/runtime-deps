package com.emmily.runtimedeps.exception;

import org.apache.maven.model.Repository;

public class UnauthorizedClientException extends RuntimeException {

  private static final String MESSAGE = "Your client is lacking credentials for the repository %s";

  public UnauthorizedClientException(Repository repository) {
    super(String.format(MESSAGE, repository.getId()));
  }

}
