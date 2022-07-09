package com.emmily.runtimedeps.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface ElementPropertyAccessor {

  static String getStringProperty(Element element,
                                  String property,
                                  int index) {
    NodeList nodes = element.getElementsByTagName(property);

    if (nodes.getLength() == 0) {
      return "";
    }

    return nodes.item(index).getTextContent();
  }

  static String getStringProperty(Element element,
                                  String property) {
    return getStringProperty(element, property, 0);
  }

  static Element getChildElement(Element parent,
                                 String element) {
    NodeList nodeList = parent.getElementsByTagName(element);

    if (nodeList.getLength() == 0) {
      return null;
    }

    Node node = nodeList.item(0);

    if (node.getNodeType() != Node.ELEMENT_NODE) {
      return null;
    }

    return (Element) node;
  }
}
