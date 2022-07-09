package com.emmily.runtimedeps.resolver.dependency;

import com.emmily.runtimedeps.dependency.FileType;
import com.emmily.runtimedeps.dependency.MavenDependency;
import com.emmily.runtimedeps.download.DependencyDownloader;
import com.emmily.runtimedeps.xml.XmlHelper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents an {@link Element}-based sub-dependency
 * resolver. It allows you to resolve the whole dependency
 * tree of a given {@link MavenDependency} by providing its
 * {@link Element pom.xml document}.
 *
 * As Maven allows developers to indirectly declare critical
 * properties of a project (such as {@code groupId}), this
 * class handles things such as XML-property-based project
 * properties by matching them via regex.
 */
public class SubDependenciesResolver implements DependencyResolver<Element> {

  private final ElementDependencyResolver delegate;
  private final DependencyDownloader dependencyDownloader;
  private final Logger logger;

  public SubDependenciesResolver(ElementDependencyResolver delegate,
                                 DependencyDownloader dependencyDownloader,
                                 Logger logger) {
    this.delegate = delegate;
    this.dependencyDownloader = dependencyDownloader;
    this.logger = logger;
  }

  /**
   * Resolves all the sub-dependencies from the given
   * {@code source pom.xml} document.
   *
   * @param source The {@code source pom.xml} to retrieve
   *               the sub-dependencies from.
   * @return The list of sub-dependencies within the given
   * {@code source pom.xml}.
   */
  @Override
  public Collection<MavenDependency> resolve(Element source) throws IOException, SAXException {
    List<MavenDependency> dependencies = new ArrayList<>(delegate.resolve(source));

    for (int i = 0; i < dependencies.size(); i++) {
      dependencies.addAll(resolve(getPom(dependencies.get(i))));
    }

    logger.info(String.format(
      "Dependencies: Successfully downloaded %s sub-dependencies",
      dependencies.size()
    ));

    return dependencies;
  }

  public Element getPom(MavenDependency dependency) throws IOException, SAXException {
    return XmlHelper.getElement(dependencyDownloader.downloadFile(dependency, FileType.POM));
  }

}
