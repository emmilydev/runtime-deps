package com.emmily.runtimedeps.exception;

import com.emmily.runtimedeps.repository.MavenRepository;

public class UnauthorizedClientException extends RuntimeException {

  private static final String MESSAGE = "Your client is lacking credentials for the repository %s";

  public UnauthorizedClientException(MavenRepository repository) {
    super(String.format(MESSAGE, repository.getId()));
  }

}
