package org.chiwooplatform.security.support;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import org.chiwooplatform.context.Constants;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * <code>
  protected void configure(HttpSecurity http) throws Exception {
    ...
    http.httpBasic().disable()
        .and().authorizeRequests()
            .antMatchers("/public/**").permitAll()
            .anyRequest().authenticated()
        .and().requestCache().requestCache(new NullRequestCache())
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .addFilterBefore( statelessTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class );
    ... 
  }
  
  &#64;Bean
  public StatelessTokenAuthenticationFilter statelessTokenAuthenticationFilter { ... }
  
 * </code>
 */
public class StatelessTokenAuthenticationFilter
        extends AbstractAuthenticationProcessingFilter {

    private final Logger logger = LoggerFactory
            .getLogger(StatelessTokenAuthenticationFilter.class);

    private OrRequestMatcher excludMatcher;

    private void filterExcludedUrls(final String... urls) {
        final List<RequestMatcher> requestMatchers = Arrays.asList(urls).stream()
                .map(v -> new AntPathRequestMatcher(v)).collect(Collectors.toList());
        excludMatcher = new OrRequestMatcher(requestMatchers);
    }

    public StatelessTokenAuthenticationFilter() {
        super("/**");
    }

    public StatelessTokenAuthenticationFilter(String filterProcessesUrl) {
        super(filterProcessesUrl);
    }

    public StatelessTokenAuthenticationFilter(String filterProcessesUrl,
            String... filterExcludedUrls) {
        super(filterProcessesUrl);
        if (filterExcludedUrls != null) {
            filterExcludedUrls(filterExcludedUrls);
        }
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request,
            HttpServletResponse response) {
        if (excludMatcher != null && excludMatcher.matches(request)) {
            return false;
        }
        return super.requiresAuthentication(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException, IOException {

        Optional<String> optionalToken = Optional
                .ofNullable(request.getHeader(Constants.AUTH_TOKEN));

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        final String restToken;
        if (authentication instanceof RestAuthenticationToken) {
            RestAuthenticationToken restAuth = (RestAuthenticationToken) authentication;
            restToken = restAuth.getToken();
        }
        else {
            restToken = null;
        }
        final String token = optionalToken.isPresent() ? optionalToken.get() : restToken;
        logger.debug("token: {}", token);
        return new RestAuthenticationToken(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getAuthenticationManager(),
                "authenticationManager must be specified");
        // Assert.notNull(this.tokenValidators, "TokenValidators must be specified");
    }
}
