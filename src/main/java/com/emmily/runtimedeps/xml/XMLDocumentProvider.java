package com.emmily.runtimedeps.xml;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class XMLDocumentProvider {

  private static final DocumentBuilder DOCUMENT_BUILDER;

  static {
    try {
      DOCUMENT_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static Document getDocument(File file) throws ParserConfigurationException, IOException, SAXException {
    return DOCUMENT_BUILDER.parse(file);
  }

  public static Document getDocument(InputStream inputStream) throws IOException, SAXException {
    return DOCUMENT_BUILDER.parse(inputStream);
  }

}
