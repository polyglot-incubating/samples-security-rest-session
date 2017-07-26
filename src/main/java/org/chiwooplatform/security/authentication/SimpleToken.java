package org.chiwooplatform.security.authentication;

public class SimpleToken {
  private final String id;

  private final Object expire;

  public SimpleToken(String token, Object expire) {
    super();
    this.id = token;
    this.expire = expire;
  }

  public String getId() {
    return id;
  }

  public Object getExpire() {
    return expire;
  }

}
