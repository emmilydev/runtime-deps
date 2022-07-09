package com.emmily.runtimedeps.auth;

import com.emmily.runtimedeps.repository.MavenRepository;

public interface Authorizer {

  void registerRepository(String id,
                          String credentials);

  default void registerRepository(MavenRepository repository,
                                  String credentials) {
    registerRepository(repository.getId(), credentials);
  }

  void unregisterRepository(String id);

  default void unregisterRepository(MavenRepository repository) {
    unregisterRepository(repository.getId());
  }

  boolean isKnown(String id);

  default boolean isKnown(MavenRepository repository) {
    return isKnown(repository.getId());
  }

  String getCredentials(String id);

  default String getCredentials(MavenRepository repository) {
    return getCredentials(repository.getId());
  }

}
