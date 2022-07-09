package com.emmily.runtimedeps.repository;

import com.emmily.runtimedeps.dependency.FileType;
import com.emmily.runtimedeps.auth.Authorizer;
import com.emmily.runtimedeps.connection.HttpConnectionBuilder;
import com.emmily.runtimedeps.dependency.MavenDependency;
import com.emmily.runtimedeps.format.UrlFormatter;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.Collection;

public interface RepositoryUtils {

  static boolean healthCheck(MavenRepository mavenRepository) throws IOException {
    try {
      HttpURLConnection connection = HttpConnectionBuilder
        .builder()
        .url(mavenRepository.getUrl())
        .method("HEAD")
        .addProperty("User-Agent", "runtime-deps")
        .build();
      connection.connect();

      return true;
    } catch (ConnectException e) {
      return false;
    }
  }

  static boolean requiresAuth(MavenRepository mavenRepository) throws IOException {
    if (mavenRepository.requiresAuth() != null) {
      return mavenRepository.requiresAuth();
    }

    HttpURLConnection connection = HttpConnectionBuilder
      .builder()
      .url(mavenRepository.getUrl())
      .addProperty("User-Agent", "runtime-deps")
      .build();
    int responseCode = connection.getResponseCode();

    boolean requiresAuth = responseCode == 401 || responseCode == 403;

    if (requiresAuth) {
      mavenRepository.setRequiresAuth(true);
    }

    return requiresAuth;
  }

  static boolean containsDependency(MavenRepository mavenRepository,
                                    MavenDependency dependency,
                                    Authorizer authorizer) throws IOException {
    MavenRepository currentMavenRepository = dependency.getRepository();

    if (currentMavenRepository != null) {
      return mavenRepository.equals(currentMavenRepository);
    }

    dependency.setRepository(mavenRepository);

    HttpConnectionBuilder connectionBuilder = HttpConnectionBuilder
      .builder()
      .url(UrlFormatter.format(dependency, FileType.JAR))
      .addProperty("User-Agent", "runtime-deps");

    if (requiresAuth(mavenRepository) && authorizer.isKnown(mavenRepository)) {
      connectionBuilder.addProperty("Authorization", authorizer.getCredentials(mavenRepository));
    }

    HttpURLConnection connection = connectionBuilder.build();
    int responseCode = connection.getResponseCode();

    if (responseCode == 404 || responseCode == 401 || responseCode == 403) {
      dependency.setRepository(null);

      return false;
    }

    return true;
  }

  static MavenRepository findRepository(MavenDependency dependency,
                                        Collection<MavenRepository> repositories,
                                        Authorizer authorizer) throws IOException {
    for (MavenRepository mavenRepository : repositories) {
      if (containsDependency(mavenRepository, dependency, authorizer)) {
        return mavenRepository;
      }
    }

    return MavenRepository.CENTRAL;
  }

}
