package com.emmily.runtimedeps.dump;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;

public interface DependencyDumper {

  /**
   * Dumps the given JAR files
   * in the underlying {@link java.net.URLClassLoader}.
   *
   * @param dependencies The dependencies to be dumped.
   */
  void dump(Collection<File> dependencies) throws MalformedURLException, ReflectiveOperationException;

  default void dump(File... dependencies) throws MalformedURLException, ReflectiveOperationException {
    dump(Arrays.asList(dependencies));
  }

}
