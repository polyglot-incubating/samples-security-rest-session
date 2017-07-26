package org.chiwooplatform.security.authentication;

public class SimpleToken {
  private final String id;

  private final Object expires;

  public SimpleToken(String id, Object expires) {
    super();
    this.id = id;
    this.expires = expires;
  }

  public String getId() {
    return id;
  }

  public Object getExpires() {
    return expires;
  }

}
