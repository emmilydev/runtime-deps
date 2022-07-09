package com.emmily.runtimedeps.resolver.dependency;

import com.emmily.runtimedeps.auth.Authorizer;
import com.emmily.runtimedeps.dependency.MavenDependency;
import com.emmily.runtimedeps.repository.MavenRepository;
import com.emmily.runtimedeps.repository.RepositoryUtils;
import com.emmily.runtimedeps.resolver.Resolver;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface DependencyResolver<S> extends Resolver<Collection<MavenDependency>, S> {

  default S parse(MavenDependency dependency) {
    return null;
  }

  /**
   * Iterates over the given {@code repositories} matching
   * any repository containing the given {@code dependency}.
   *
   * @param dependency   The dependency to be searched.
   * @param repositories The repository list where to search,
   *                     priority-sorted.
   * @param authorizer   The credential provider.
   * @return The corresponding repository, if found.
   * @throws NullPointerException If none of the given {@code repositories}
   *                              contain the given {@code dependency}.
   */
  static MavenRepository findRepository(MavenDependency dependency,
                                         List<MavenRepository> repositories,
                                         Authorizer authorizer) throws IOException {
    repositories.sort(MavenRepository::compareTo);

    for (MavenRepository repository : repositories) {
      if (RepositoryUtils.containsDependency(repository, dependency, authorizer)) {
        return repository;
      }

    }

    throw new NullPointerException(String.format(
      "Dependencies: The repository of the dependency %s is unknown",
      dependency.getArtifactId()
    ));
  }

}
