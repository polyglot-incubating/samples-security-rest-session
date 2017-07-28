package org.chiwooplatform.security.authentication;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class SimpleToken {
    private String token;

    private Long expires;

    public SimpleToken() {
        super();
    }

    public SimpleToken(String token, Long expires) {
        super();
        this.token = token;
        this.expires = expires;
    }

    // {
    // "expires" : NumberLong(1501214752628),
    // "token" : "2eac15e5-a08a-4018-96de-8099cee7c2de"
    // }
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

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

}
