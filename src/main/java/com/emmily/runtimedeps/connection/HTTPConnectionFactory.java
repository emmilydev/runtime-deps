package com.emmily.runtimedeps.connection;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public interface HTTPConnectionFactory {

  /**
   * Creates an HTTP connection to the given {@code url},
   * using the given {@code userAgent} and the given {@code authorizationHeader}.
   * It allows you to implement different types of auth schemas.
   *
   * @param url The destination URL.
   * @param userAgent The request's User-Agent.
   * @param authorizationHeader The request's Authorization header
   * @return The newly created connection.
   */
  static HttpURLConnection createConnection(String url,
                                            String userAgent,
                                            @Nullable String authorizationHeader) {
    try {
      URL actualURL = new URL(url);
      HttpURLConnection connection = (HttpURLConnection) actualURL.openConnection();
      connection.setRequestProperty("User-Agent", userAgent);

      if (authorizationHeader != null) {
        connection.setRequestProperty("Authorization", authorizationHeader);
      }

      return connection;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static HttpURLConnection createConnection(String url,
                                            String userAgent) {
    return createConnection(url, userAgent, null);
  }

  /**
   * Calls {@link #createConnection(String, String, String)}, setting
   * the {@code authorizationHeader} parameter to a BASIC scheme.
   */
  static HttpURLConnection createConnection(String url,
                                            String userAgent,
                                            String username,
                                            String password) {
    return createConnection(
      url,
      userAgent,
      Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8))
    );
  }

}
