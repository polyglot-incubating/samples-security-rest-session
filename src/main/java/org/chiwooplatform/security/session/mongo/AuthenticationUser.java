package org.chiwooplatform.security.session.mongo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.security.authentication.SimpleToken;
import org.springframework.util.StringUtils;

public class AuthenticationUser
{
    private String username;

    private Integer userId;

    public AuthenticationUser()
    {
        super();
    }

    public AuthenticationUser( AuthenticationUser oldUser )
    {
        super();
        this.username = oldUser.getUsername();
        this.userId = oldUser.getUserId();
        this.authorities = oldUser.getAuthorities();
        this.tokens = oldUser.activeTokens();
    }

    private Collection<SimpleToken> tokens = new HashSet<>(); /* token. It can be modified with new token */

    private Collection<String> authorities;

    public void authentication( final String token, final Long expires )
    {
        if ( StringUtils.isEmpty( token ) || DateUtils.isExpired( expires ) )
        {
            return;
        }
        final SimpleToken simpleToken = new SimpleToken( token, expires );
        tokens.add( simpleToken );
    }

    public Collection<SimpleToken> activeTokens()
    {
        if ( this.tokens.size() > 0 )
        {
            final List<SimpleToken> tokens = this.tokens.stream()
                                                        // .filter( ( v ) -> !DateUtils.isExpired( (Long) v.getExpires() ) )
                                                        .collect( Collectors.toList() );
            return tokens;
        }
        return this.tokens;
    }

    @Override
    public String toString()
    {
        return "AuthenticationUser [username=" + username + ", userId=" + userId + ", tokens=" + tokens
            + ", authorities=" + authorities + "]";
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId( Integer userId )
    {
        this.userId = userId;
    }

    public Collection<SimpleToken> getTokens()
    {
        return tokens;
    }

    public Collection<String> getAuthorities()
    {
        return authorities;
    }

    public void setAuthorities( Collection<String> authorities )
    {
        this.authorities = authorities;
    }
}
