package com.emmily.runtimedeps.connection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpConnectionBuilder {

  public static HttpConnectionBuilder builder() {
    return new HttpConnectionBuilder();
  }

  private String url;
  private Map<String, String> properties;
  private int readTimeout = 10000;
  private int connectionTimeout = 10000;
  private String method;

  private HttpConnectionBuilder() {}

  public HttpConnectionBuilder url(String url) {
    this.url = url;
    
    return this;
  }
  
  public HttpConnectionBuilder addProperty(String name,
                                           String value) {
    if (properties == null) {
      properties = new ConcurrentHashMap<>();
    }

    if (name == null || value == null) {
      return this;
    }

    properties.put(name, value);
    
    return this;
  }
  
  public HttpConnectionBuilder readTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
    
    return this;
  }
  
  public HttpConnectionBuilder connectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
    
    return this;
  }

  public HttpConnectionBuilder method(String method) {
    this.method = method;

    return this;
  }

  public HttpURLConnection build() throws IOException {
    if (url == null) {
      throw new IllegalArgumentException("url cannot be null");
    }
    
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setConnectTimeout(connectionTimeout);
    connection.setReadTimeout(readTimeout);

    if (method != null) {
      connection.setRequestMethod(method);
    }

    if (properties != null) {
      for (Map.Entry<String, String> property : properties.entrySet()) {
        connection.setRequestProperty(property.getKey(), property.getValue());
      }
    }

    return connection;
  }

}
