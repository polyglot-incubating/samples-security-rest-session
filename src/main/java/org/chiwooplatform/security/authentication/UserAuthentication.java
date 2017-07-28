package org.chiwooplatform.security.authentication;

public class UserAuthentication {
    private String username;
    private SimpleToken token;

    public UserAuthentication(String username, String token, Long expires) {
        this.username = username;
        this.token = new SimpleToken(token, expires);
    }

    public String getUsername() {
        return username;
    }

    public SimpleToken getToken() {
        return token;
    }
}
