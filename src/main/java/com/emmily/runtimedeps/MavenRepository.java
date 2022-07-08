package com.emmily.runtimedeps;

import org.apache.maven.model.Repository;

public class MavenRepository extends Repository {

  public static final MavenRepository CENTRAL = new MavenRepository(
    "central",
    "https://repo1.maven.org/maven2/"
  );

  private final String id;
  private final String url;

  public MavenRepository(String id,
                         String url) {
    this.id = id;
    this.url = url;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getUrl() {
    return url;
  }
}
