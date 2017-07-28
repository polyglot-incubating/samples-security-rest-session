package org.chiwooplatform.security.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.StringUtils;

import org.chiwooplatform.context.support.DateUtils;

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

    public AuthenticationUser newUser() {
        AuthenticationUser o = new AuthenticationUser();
        o.setId(this.id);
        o.setUserId(this.userId);
        o.setAuthorities(authorities);
        return o;
    }

    public boolean authentication(final String token, final Long expires) {
        if (StringUtils.isEmpty(token) || DateUtils.isExpired(expires)) {
            return false;
        }
        final SimpleToken simpleToken = new SimpleToken(token, expires);
        tokens.add(simpleToken);
        return true;
    }

    @Override
    public String toString() {
        return "AuthenticationUser [id=" + id + ", userId=" + userId + ", tokens="
                + tokens + ", authorities=" + authorities + "]";
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

    public void addTokens(List<SimpleToken> tokens) {
        if (this.tokens == null) {
            this.tokens = new ArrayList<>();
        }
        this.tokens.addAll(tokens);
    }

    public Collection<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<String> authorities) {
        this.authorities = authorities;
    }

}
