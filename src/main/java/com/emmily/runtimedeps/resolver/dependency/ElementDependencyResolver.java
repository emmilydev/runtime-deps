package com.emmily.runtimedeps.resolver.dependency;

import com.emmily.runtimedeps.auth.Authorizer;
import com.emmily.runtimedeps.dependency.FileType;
import com.emmily.runtimedeps.dependency.MavenDependency;
import com.emmily.runtimedeps.download.DependencyDownloader;
import com.emmily.runtimedeps.exception.DependencyNotFoundException;
import com.emmily.runtimedeps.repository.MavenRepository;
import com.emmily.runtimedeps.resolver.Resolver;
import com.emmily.runtimedeps.xml.IterableNodeList;
import com.emmily.runtimedeps.xml.XmlHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.emmily.runtimedeps.xml.ElementPropertyAccessor.getChildElement;
import static com.emmily.runtimedeps.xml.ElementPropertyAccessor.getStringProperty;

/**
 * Represents an {@link Element}-based dependency
 * resolver. It allows you to resolve the dependency
 * tree of a given {@link MavenDependency} by providing its
 * {@link Element pom.xml document}.
 *
 * As Maven allows developers to indirectly declare critical
 * properties of a project (such as {@code groupId}), this
 * class handles things such as XML-property-based project
 * properties by matching them via regex.
 *
 * <p>
 * Note that this implementation does not have a "deep"
 * dependency resolution i.e. sub-dependency resolution.
 * <strong>If you want to resolve sub-dependencies, you must
 * use {@link SubDependenciesResolver}.</strong>
 * </p>
 */
public class ElementDependencyResolver implements DependencyResolver<Element> {

  private static final Pattern PROJECT_PROPERTY_PATTERN = Pattern.compile("\\{(\\w+|\\w+\\.\\w+)}");
  private static final Collection<String> MAVEN_BUILTIN_PROPERTIES = Arrays.asList(
    "modelVersion",
    "parent",
    "groupId",
    "artifactId",
    "version",
    "packaging",
    "name",
    "description",
    "url",
    "childProjectUrlInheritAppendPath",
    "inceptionYear",
    "licenses",
    "developers",
    "contributors",
    "mailingLists",
    "prerequisites",
    "scm",
    "issueManagement",
    "ciManagement",
    "build",
    "profiles",
    "modelEncoding"
  );

  private final Collection<String> exclusions;
  private final Logger logger;
  private final List<MavenRepository> knownRepositories;
  private final Authorizer authorizer;
  private final DependencyDownloader dependencyDownloader;

  public ElementDependencyResolver(Collection<String> exclusions,
                                   Logger logger,
                                   List<MavenRepository> knownRepositories,
                                   Authorizer authorizer,
                                   DependencyDownloader dependencyDownloader) {
    this.exclusions = exclusions;
    this.logger = logger;
    this.knownRepositories = knownRepositories;
    this.authorizer = authorizer;
    this.dependencyDownloader = dependencyDownloader;
  }

  @Override
  public Collection<MavenDependency> resolve(Element source) throws IOException, SAXException {
    NodeList rawDependencyList = source.getElementsByTagName("dependency");
    int dependenciesSize = rawDependencyList.getLength();

    if (dependenciesSize == 0) {
      logger.warning("Dependencies: The given pom.xml document doesn't contain any dependency.");

      return Collections.emptySet();
    }

    Collection<MavenDependency> dependencies = new ArrayList<>();

    for (int i = 0; i < rawDependencyList.getLength(); i++) {
      Node node = rawDependencyList.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element dependencyElement = (Element) node;
      String scope = getStringProperty(dependencyElement, "scope");

      if (!scope.equals("compile")) {
        continue;
      }

      String groupId = checkProperty("groupId", dependencyElement);

      if (exclusions.contains(groupId)) {
        continue;
      }

      MavenDependency subDependency = new MavenDependency(
        groupId,
        checkProperty("artifactId", dependencyElement),
        checkProperty("version", dependencyElement),
        null,
        null
      );

      subDependency.setRepository(DependencyResolver.findRepository(
        subDependency,
        knownRepositories,
        authorizer
      ));

      dependencies.add(subDependency);
    }

    logger.info(String.format(
      "Dependencies: Resolved %s dependencies",
      dependencies.size()
    ));

    return dependencies;
  }

  /**
   * Checks that the given {@code property} can be used
   * to build a URL. If not, it's parsed.
   *
   * @param property The property to be checked (and parsed)
   * @param dependencyElement The dependency element.
   * @return The parsed property.
   */
  private String checkProperty(String property,
                               Element dependencyElement) throws IOException, SAXException {
    String original = property;
    property = getStringProperty(dependencyElement, property);

    // It can be used to build a URL.
    if (!property.equals("") && !PROJECT_PROPERTY_PATTERN.matcher(property).matches()) {
      return property;
    }

    // Sometimes developers don't define the dependency version,
    // so we have to find it
    if (property.equals("") && original.equals("version")) {
      MavenDependency dependency = new MavenDependency(
        getStringProperty(dependencyElement, "groupId"),
        getStringProperty(dependencyElement, "artifactId"),
        null,
        null,
        null
      );

      dependency.setRepository(DependencyResolver.findRepository(
        dependency,
        knownRepositories,
        authorizer
      ));

      Element mavenMetadataElement = XmlHelper.getElement(dependencyDownloader.downloadFile(
        dependency,
        FileType.MAVEN_METADATA
      ));

      return getStringProperty(mavenMetadataElement, "latest");
    }

    Element element = getChildElement(dependencyElement, property);

    if (element == null) {
      // Might be a Maven property such as version
      if (property.startsWith("${project.") && property.endsWith("}") && MAVEN_BUILTIN_PROPERTIES.contains(property)) {
        String fixedProperty = property
          .replace("${project.", "")
          .replace("}", "");

        return checkProperty(
          fixedProperty,
          dependencyElement
        );
      }

      return checkProperty(
        property,
        getParentPom(dependencyElement)
      );
    }

    return checkProperty(
      property,
      element
    );
  }

  private Element getParentPom(Element dependencyElement) throws IOException, SAXException {
    Element element = getChildElement(dependencyElement, "parent");

    if (element == null) {
      throw new DependencyNotFoundException(getStringProperty(dependencyElement, "artifactId"), FileType.POM);
    }

    Optional<MavenDependency> optionalParent = resolve(element).stream().findAny();

    if (!optionalParent.isPresent()) {
      throw new DependencyNotFoundException(getStringProperty(element, "artifactId"), FileType.POM);
    }

    return XmlHelper.getElement(dependencyDownloader.downloadFile(optionalParent.get(), FileType.POM));
  }
}
