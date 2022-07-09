package com.emmily.runtimedeps.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Wraps a {@link NodeList}, allowing you to iterate over
 * its elements using a {@code for-each} loop instead of using
 * a classic {@code for}.
 */
public class IterableNodeList implements Iterable<Node> {

  private final NodeList delegate;

  public IterableNodeList(NodeList delegate) {
    this.delegate = delegate;
  }

  @Override
  public Iterator<Node> iterator() {
    Collection<Node> nodes = new HashSet<>();

    for (int i = 0; i < delegate.getLength(); i++) {
      nodes.add(delegate.item(i));
    }

    return nodes.iterator();
  }

}
