package com.emmily.runtimedeps.auth;

import org.apache.maven.model.Repository;

public interface Authorizer {

  void registerRepository(String id,
                          String credentials);

  default void registerRepository(Repository repository,
                                  String credentials) {
    registerRepository(repository.getId(), credentials);
  }

  void unregisterRepository(String id);

  default void unregisterRepository(Repository repository) {
    unregisterRepository(repository.getId());
  }

  boolean isKnown(String id);

  default boolean isKnown(Repository repository) {
    return isKnown(repository.getId());
  }

  String getCredentials(String id);

  default String getCredentials(Repository repository) {
    return getCredentials(repository.getId());
  }

}
