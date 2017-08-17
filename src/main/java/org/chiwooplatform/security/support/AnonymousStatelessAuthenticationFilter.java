package org.chiwooplatform.security.support;

import java.util.List;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

public class AnonymousStatelessAuthenticationFilter extends AnonymousAuthenticationFilter {

    private final String key;

    private static final Object principal = "anonymousUser";

    private static final List<GrantedAuthority> AUTHORITIES = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");

    public AnonymousStatelessAuthenticationFilter(String key) {
        super(key, AnonymousStatelessAuthenticationFilter.principal,
                AnonymousStatelessAuthenticationFilter.AUTHORITIES);
        this.key = key;
    }

    protected Authentication createAuthentication(HttpServletRequest request) {
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(key,
                AnonymousStatelessAuthenticationFilter.principal, AnonymousStatelessAuthenticationFilter.AUTHORITIES);
        // auth.setAuthenticated( false );
        return auth;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(createAuthentication((HttpServletRequest) req));
            if (logger.isDebugEnabled()) {
                logger.debug("Populated SecurityContextHolder with anonymous token: '"
                        + SecurityContextHolder.getContext().getAuthentication() + "'");
            }
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("SecurityContextHolder not populated with anonymous token, as it already contained: '"
                        + SecurityContextHolder.getContext().getAuthentication() + "'");
            }
        }
        chain.doFilter(req, res);
    }
}
