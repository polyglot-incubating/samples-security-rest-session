package org.chiwooplatform.security.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "authenticationUser")
public class AuthenticationUser {

    private String id;

    private Integer userId;

    /*
     * token. It can be modified with new token
     */
    private List<SimpleToken> tokens = new ArrayList<>();

    private Collection<String> authorities;

    public AuthenticationUser() {
        super();
    }

    public AuthenticationUser newUser(final List<SimpleToken> newTokens) {
        List<SimpleToken> activeTokens = this.activeTokens();
        activeTokens.addAll(newTokens);
        AuthenticationUser o = new AuthenticationUser();
        o.setId(this.id);
        o.setUserId(this.userId);
        o.setAuthorities(authorities);
        o.setTokens(activeTokens);
        return o;
    }

    @Override
    public String toString() {
        return "AuthenticationUser [id=" + id + ", userId=" + userId + ", tokens=" + tokens + ", authorities="
                + authorities + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<SimpleToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<SimpleToken> tokens) {
        this.tokens = tokens;
    }

    private List<SimpleToken> activeTokens() {
        if (getTokens() == null) {
            return new ArrayList<>();
        }
        final long currentTimestamp = System.currentTimeMillis();
        final List<SimpleToken> activeTokens = getTokens().stream().filter((t) -> t.getExpires() > currentTimestamp)
                .collect(Collectors.toList());
        return activeTokens;
    }

    public void addToken(final SimpleToken token) {
        if (this.tokens == null) {
            this.tokens = new ArrayList<>();
        }
        this.tokens.add(token);
    }

    public Collection<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<String> authorities) {
        this.authorities = authorities;
    }

}
