package com.emmily.runtimedeps.download.version;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyVersionResolver {

  private static final Pattern VERSION_PATTERN = Pattern.compile("[+]?\\d+\\.[+]?\\d+\\.[+]?\\d+/");

  public static String getLatestVersion(HttpURLConnection connection) {
    try {
      Scanner scanner = new Scanner(connection.getInputStream()).useDelimiter("\\A");
      String html = scanner.next();

      Matcher matcher = VERSION_PATTERN.matcher(html);
      String version = null;

      while (matcher.find()) {
        version = html.substring(matcher.start(), matcher.end());
      }

      if (version == null) {
        return null;
      }

      return version.replace("/", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
