package org.chiwooplatform.security.authentication;

public class SimpleToken {
  private String token;

  private Object expires;

  public SimpleToken() {
    super();
  }

  public SimpleToken(String token, Object expires) {
    super();
    this.token = token;
    this.expires = expires;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SimpleToken [token=");
    builder.append(token);
    builder.append(", expires=");
    builder.append(expires);
    builder.append("]");
    return builder.toString();
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Object getExpires() {
    return expires;
  }

  public void setExpires(Object expires) {
    this.expires = expires;
  }


}
