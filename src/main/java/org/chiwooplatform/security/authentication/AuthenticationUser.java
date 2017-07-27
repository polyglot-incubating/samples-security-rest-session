package org.chiwooplatform.security.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.chiwooplatform.context.support.DateUtils;
import org.springframework.util.StringUtils;

public class AuthenticationUser {

    private String id;

    private Integer userId;

    private List<SimpleToken> tokens; /* token. It can be modified with new token */

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

    public void authentication(final String token, final Long expires) {
        if (StringUtils.isEmpty(token) || DateUtils.isExpired(expires)) {
            return;
        }
        final SimpleToken simpleToken = new SimpleToken(token, expires);
        if (tokens == null) {
            tokens = new ArrayList<>();
        }
        tokens.add(simpleToken);
    }

    public List<SimpleToken> activeTokens() {
        if (this.tokens.size() > 0) {
            final List<SimpleToken> tokens = this.tokens.stream()
                    // .filter( ( v ) -> !DateUtils.isExpired( (Long) v.getExpires() ) )
                    .collect(Collectors.toList());
            return tokens;
        }
        return this.tokens;
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

    public Collection<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<String> authorities) {
        this.authorities = authorities;
    }

}
