package com.emmily.runtimedeps.load;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class DependencyLoader {

  private static final Method ADD_URL;

  static {
    try {
      ADD_URL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      ADD_URL.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private final URLClassLoader classLoader;

  public DependencyLoader(ClassLoader classLoader) {
    if (!(classLoader instanceof URLClassLoader)) {
      throw new IllegalArgumentException("Dependencies: In order to use runtime-deps you need to have a " +
        "URLClassLoader.");
    }

    this.classLoader = (URLClassLoader) classLoader;
  }

  public void loadDependencies(List<File> dependencies) throws MalformedURLException, ReflectiveOperationException {
    for (File file : dependencies) {
      ADD_URL.invoke(classLoader, file.toURI().toURL());
    }
  }

}
