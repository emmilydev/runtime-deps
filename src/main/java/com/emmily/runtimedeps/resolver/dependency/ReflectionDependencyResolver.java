package com.emmily.runtimedeps.resolver.dependency;

import com.emmily.runtimedeps.auth.Authorizer;
import com.emmily.runtimedeps.dependency.Dependencies;
import com.emmily.runtimedeps.dependency.Dependency;
import com.emmily.runtimedeps.dependency.MavenDependency;
import com.emmily.runtimedeps.repository.MavenRepository;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Represents a reflection-based {@link MavenDependency}
 * resolver. It allows you to scan a given class in order
 * to find declared dependencies. This resolver requires
 * your classes to be annotated with {@link Dependencies @Dependencies}
 * and, therefore, {@link Dependency @Dependency} too.
 *
 * <p>
 * Note that this implementation does not have a "deep"
 * dependency resolution i.e. sub-dependency resolution.
 * <strong>If you want to resolve sub-dependencies, you must
 * use {@link SubDependenciesResolver}.</strong>
 * </p>
 */
public class ReflectionDependencyResolver implements DependencyResolver<Class<?>> {

  private final Collection<String> exclusions;
  private final Logger logger;
  private final List<MavenRepository> knownRepositories;
  private final Authorizer authorizer;

  public ReflectionDependencyResolver(Collection<String> exclusions,
                                      Logger logger,
                                      List<MavenRepository> knownRepositories,
                                      Authorizer authorizer) {
    this.exclusions = exclusions;
    this.logger = logger;
    this.knownRepositories = knownRepositories;
    this.authorizer = authorizer;
  }

  /**
   * Resolves all the dependencies in the given {@code source}
   * class without a deep resolution policy, i.e., it doesn't
   * resolve sub-dependencies.
   *
   * @param source The source class to scan.
   * @return The list of dependencies declared in the
   * given {@code source} class.
   */
  @Override
  public Collection<MavenDependency> resolve(Class<?> source) throws IOException {
    Dependencies declaredDependencies = source.getAnnotation(Dependencies.class);

    if (declaredDependencies == null) {
      logger.warning("Dependencies: Unable to resolve repositories in the source class " + source.getName());

      return Collections.emptySet();
    }

    Collection<MavenDependency> dependencies = new ArrayList<>();

    for (Dependency annotation : declaredDependencies.value()) {
      if (exclusions.contains(annotation.groupId()) || exclusions.contains(annotation.artifactId())) {
        continue;
      }

      MavenDependency dependency = new MavenDependency(
        annotation.groupId(),
        annotation.artifactId(),
        annotation.version(),
        null,
        null
      );

      dependency.setRepository(DependencyResolver.findRepository(
        dependency,
        knownRepositories,
        authorizer
      ));

      dependencies.add(dependency);
    }

    return dependencies;
  }

}
