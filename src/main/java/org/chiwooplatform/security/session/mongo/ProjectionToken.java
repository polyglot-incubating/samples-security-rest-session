package org.chiwooplatform.security.session.mongo;

import java.util.List;

import org.chiwooplatform.security.authentication.SimpleToken;

public class ProjectionToken {

  private String id;

  private List<SimpleToken> tokens;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ProjectionToken [id=");
    builder.append(id);
    builder.append(", tokens=");
    builder.append(tokens);
    builder.append("]");
    return builder.toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<SimpleToken> getTokens() {
    return tokens;
  }

  public void setTokens(List<SimpleToken> tokens) {
    this.tokens = tokens;
  }

}
