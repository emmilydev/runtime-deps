package com.emmily.runtimedeps.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class XmlHelper {

  private static final DocumentBuilder DOCUMENT_BUILDER;

  static {
    try {
      DOCUMENT_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static Document getDocument(File file) throws IOException, SAXException {
    return DOCUMENT_BUILDER.parse(file);
  }

  public static Element getElement(File file) throws IOException, SAXException {
    return getDocument(file).getDocumentElement();
  }
}
