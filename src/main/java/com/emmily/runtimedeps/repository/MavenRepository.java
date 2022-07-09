package com.emmily.runtimedeps.repository;

public class MavenRepository implements Comparable<MavenRepository> {

  public static final MavenRepository CENTRAL = new MavenRepository(
    "central",
    "https://repo1.maven.org/maven2/"
  );

  private final String id;
  private final String url;
  private Boolean requiresAuth = null;

  public MavenRepository(String id,
                         String url) {
    this.id = id;
    this.url = url;
  }

  public String getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public Boolean requiresAuth() {
    return requiresAuth;
  }

  public void setRequiresAuth(Boolean requiresAuth) {
    this.requiresAuth = requiresAuth;
  }

  @Override
  public String toString() {
    return "MavenRepository{" +
      "id='" + id + '\'' +
      ", url='" + url + '\'' +
      '}';
  }

  @Override
  public int compareTo(MavenRepository o) {
    if (o.requiresAuth) {
      if (requiresAuth) {
        return 0;
      } else {
        return 1;
      }
    } else {
      if (requiresAuth) {
        return -1;
      }

      return 0;
    }
  }

}
