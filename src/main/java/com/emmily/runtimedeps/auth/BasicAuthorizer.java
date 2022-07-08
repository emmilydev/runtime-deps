package com.emmily.runtimedeps.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BasicAuthorizer implements Authorizer {

  private final Map<String, String> credentials;

  public BasicAuthorizer(Map<String, String> credentials) {
    this.credentials = credentials;
  }

  public BasicAuthorizer() {
    this(new HashMap<>());
  }

  @Override
  public void registerRepository(String id,
                                 String credentials) {
    if (this.credentials.containsKey(credentials)) {
      this.credentials.replace(id, credentials);
    }

    this.credentials.put(id, credentials);
  }

  @Override
  public void unregisterRepository(String id) {
    credentials.remove(id);
  }

  @Override
  public boolean isKnown(String id) {
    return credentials.containsKey(id);
  }

  @Override
  public String getCredentials(String id) {
    return "Basic " + Base64.getEncoder().encodeToString(credentials.get(id).getBytes(StandardCharsets.UTF_8));
  }

}
