package com.emmily.runtimedeps.download.version;

import com.emmily.runtimedeps.xml.XMLDocumentProvider;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class DependencyVersionResolver {

  public static String getLatestVersion(HttpURLConnection connection) throws IOException, SAXException {
    try (
      InputStream inputStream = connection.getInputStream()
    ) {
      Document document = XMLDocumentProvider.getDocument(inputStream);

      return document.getElementsByTagName("latest").item(0).getTextContent();
    }
  }

}
