package com.emmily.runtimedeps.exception;

import com.emmily.runtimedeps.repository.MavenRepository;

public class RepositoryOfflineException extends IllegalStateException {

  private static final String MESSAGE = "The repository %s has failed the health check\n" +
    "This could be because:\n" +
    " a) The server is offline" +
    " b) You don't have internet connection";

  public RepositoryOfflineException(MavenRepository repository) {
    super(String.format(MESSAGE, repository.getId()));
  }

}
