package com.emmily.runtimedeps.test;

import com.emmily.runtimedeps.auth.Authorizer;
import com.emmily.runtimedeps.auth.BasicAuthorizer;
import com.emmily.runtimedeps.dependency.Dependencies;
import com.emmily.runtimedeps.dependency.Dependency;
import com.emmily.runtimedeps.dependency.MavenDependency;
import com.emmily.runtimedeps.download.DefaultDependencyDownloader;
import com.emmily.runtimedeps.download.DependencyDownloader;
import com.emmily.runtimedeps.dump.DefaultDependencyDumper;
import com.emmily.runtimedeps.dump.DependencyDumper;
import com.emmily.runtimedeps.repository.MavenRepository;
import com.emmily.runtimedeps.repository.Repositories;
import com.emmily.runtimedeps.repository.Repository;
import com.emmily.runtimedeps.resolver.dependency.DependencyResolver;
import com.emmily.runtimedeps.resolver.dependency.ReflectionDependencyResolver;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

@Repositories(
  @Repository(
    id = "central",
    url = "https://repo1.maven.org/maven2/"
  )
)
@Dependencies({
  @Dependency(
    groupId = "com.google.guava",
    artifactId = "guava",
    version = "31.1-jre"
  ),
  @Dependency(
    groupId = "org.mongodb",
    artifactId = "mongodb-driver-sync",
    version = "4.6.1"
  )
})
public class DownloadTest {

  @Test
  public void runTest() throws IOException, SAXException {
    Logger logger = Logger.getLogger("RuntimeDeps");

    Collection<String> exclusions = Arrays.asList(
      "junit",
      "hamcrest",
      "easymock",
      "org.junit.*"
    );

    List<MavenRepository> knownRepositories = Collections.singletonList(MavenRepository.CENTRAL);

    Authorizer authorizer = new BasicAuthorizer();

    DependencyResolver<Class<?>> reflectionResolver = new ReflectionDependencyResolver(
      exclusions,
      logger,
      knownRepositories,
      authorizer
    );
    DependencyDownloader dependencyDownloader = new DefaultDependencyDownloader(
      new File(System.getProperty("user.home") + "/deps/"),
      logger,
      authorizer,
      exclusions,
      knownRepositories
    );

    ClassLoader classLoader = new URLClassLoader(new URL[0], getClass().getClassLoader());

    DependencyDumper dependencyDumper = new DefaultDependencyDumper(classLoader);

    Collection<MavenDependency> dependencies = reflectionResolver.resolve(getClass());
    Collection<File> downloaded = new ArrayList<>();

    for (MavenDependency dependency : dependencies) {
      downloaded.addAll(dependencyDownloader.downloadAll(dependency));
    }

    System.out.println(downloaded);
  }

}
