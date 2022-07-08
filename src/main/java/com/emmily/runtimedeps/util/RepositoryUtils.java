package com.emmily.runtimedeps.util;

import com.emmily.runtimedeps.MavenDependency;
import com.emmily.runtimedeps.MavenRepository;
import com.emmily.runtimedeps.auth.Authorizer;
import com.emmily.runtimedeps.connection.HttpConnectionBuilder;
import org.apache.maven.model.Repository;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.Collection;

public interface RepositoryUtils {

  static boolean healthCheck(Repository repository) throws IOException {
    try {
      HttpURLConnection connection = HttpConnectionBuilder
        .builder()
        .url(repository.getUrl())
        .method("HEAD")
        .addProperty("User-Agent", "runtime-deps")
        .build();
      connection.connect();

      return true;
    } catch (ConnectException e) {
      return false;
    }
  }

  static boolean requiresAuth(Repository repository) throws IOException {
    HttpURLConnection connection = HttpConnectionBuilder
      .builder()
      .url(repository.getUrl())
      .addProperty("User-Agent", "runtime-deps")
      .build();
    int responseCode = connection.getResponseCode();

    return responseCode == 401 || responseCode == 403;
  }

  static boolean containsDependency(Repository repository,
                                    MavenDependency dependency,
                                    Authorizer authorizer) throws IOException {
    Repository currentRepository = dependency.getRepository();

    if (currentRepository != null) {
      return repository.equals(currentRepository);
    }

    dependency.setRepository(repository);

    HttpConnectionBuilder connectionBuilder = HttpConnectionBuilder
      .builder()
      .url(dependency.getJarUrl())
      .addProperty("User-Agent", "runtime-deps");

    if (requiresAuth(repository) && authorizer.isKnown(repository)) {
      connectionBuilder.addProperty("Authorization", authorizer.getCredentials(repository));
    }

    HttpURLConnection connection = connectionBuilder.build();
    int responseCode = connection.getResponseCode();

    if (responseCode == 404 || responseCode == 401 || responseCode == 403) {
      dependency.setRepository(null);

      return false;
    }

    return true;
  }

  static Repository findRepository(MavenDependency dependency,
                                   Collection<Repository> repositories,
                                   Authorizer authorizer) throws IOException {
    for (Repository repository : repositories) {
      if (containsDependency(repository, dependency, authorizer)) {
        return repository;
      }
    }

    return MavenRepository.CENTRAL;
  }

}
