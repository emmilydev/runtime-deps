package com.emmily.runtimedeps.dump;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

public class DefaultDependencyDumper implements DependencyDumper {

  /**
   * According to this article: https://www.optaplanner.org/blog/2018/01/09/JavaReflectionButMuchFaster.html#:~:text=Well%20unfortunately%2C%20MethodHandle%20is%20even,136%25%20slower%20than%20direct%20access.
   * Method handles are slower than reflection in Java 8, and they're significantly harder to use.
   */
  private static final Method ADD_URL;

  static {
    try {
      ADD_URL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      ADD_URL.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Dependencies: Unable to find the addURL method in URLClassLoader. " +
        "Check your JRE (DefaultDependencyDumper works for Java 8 only).");
    }
  }

  private final ClassLoader classLoader;

  public DefaultDependencyDumper(ClassLoader classLoader) {
    if (!(classLoader instanceof URLClassLoader)) {
      throw new IllegalArgumentException("Dependencies: you need an URLClassLoader to use runtime-deps.");
    }

    this.classLoader = classLoader;
  }

  @Override
  public void dump(Collection<File> dependencies) throws MalformedURLException, ReflectiveOperationException {
    for (File file : dependencies) {
      ADD_URL.invoke(classLoader, file.toURI().toURL());
    }
  }
}
