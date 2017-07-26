package org.chiwooplatform.security.support;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.authentication.SimpleCredentials;
import org.chiwooplatform.security.support.web.RestAuthenticationFailureHandler;
import org.chiwooplatform.security.support.web.RestAuthenticationSuccessHandler;
import org.chiwooplatform.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 
 * AbstractAuthenticationProcessingFilter 를 상속하는 필터는 오직 사용자 인증 기능만을 담당 한다. <code>
&#64;EnableRedisSessionRegistry
&#64;EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  protected void configure(HttpSecurity http) throws Exception {
    ...
    http.httpBasic().disable()
        .and().authorizeRequests()
            .antMatchers("/public/**").permitAll()
            .anyRequest().authenticated()
        .and().requestCache().requestCache(new NullRequestCache())
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .maximumSessions(2).maxSessionsPreventsLogin(true)
            .sessionRegistry(sessionRegistry).and()
        .addFilterBefore( restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class );
    ... 
  }
  
  &#64;Bean
  public RestAuthenticationFilter restAuthenticationFilter { ... }
  
  
 * </code>
 */
public class RestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private final Logger logger = LoggerFactory.getLogger(RestAuthenticationFilter.class);

  private ObjectMapper objectMapper;

  private boolean postOnly = true;



  private void postProcess() {
    if (this.objectMapper == null) {
      this.objectMapper = new ObjectMapper();
    }
    setAuthenticationFailureHandler(new RestAuthenticationFailureHandler());
    setAuthenticationSuccessHandler(new RestAuthenticationSuccessHandler(this.objectMapper));
  }

  public RestAuthenticationFilter() {
    this("/login");
  }

  public RestAuthenticationFilter(String loginProcessUri) {
    super(new AntPathRequestMatcher(loginProcessUri, "POST"));
  }

  public RestAuthenticationFilter(String loginProcessUri, ObjectMapper objectMapper) {
    super(new AntPathRequestMatcher(loginProcessUri, "POST"));
    this.objectMapper = objectMapper;
  }

  private RestAuthenticationToken getRestAuthenticationToken(HttpServletRequest request) {
    RestAuthenticationToken authenticationToken;
    try {
      SimpleCredentials credentials =
          this.objectMapper.readValue(request.getReader(), SimpleCredentials.class);
      final String principal = credentials.getUsername();
      final String password = credentials.getPassword();
      final String token = request.getSession().getId();
      authenticationToken = new RestAuthenticationToken(principal, password, token);
    } catch (Exception e) {
      authenticationToken = new RestAuthenticationToken("");
    }
    authenticationToken.setAgent(WebUtils.userAgent(request));
    return authenticationToken;
  }


  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException {
    logger.info("request.getMethod(): {}", request.getMethod());
    if (postOnly && !request.getMethod().equals("POST")) {
      throw new AuthenticationServiceException(
          "Authentication method not supported: " + request.getMethod());
    }
    if (!WebUtils.isAjaxRequest(request)) {
      throw new AuthenticationServiceException(
          "Authentication method only supported XMLHttpRequest."
              + request.getHeader("X-Requested-With"));
    }
    RestAuthenticationToken authRequest = getRestAuthenticationToken(request);

    // 인증을 성공 했다면 response 의 location 헤더에 restapi 를 알려주면 좋겠지...
    // final URI location = WebUtils.uriLocation("/{id}", newAuthentication.getName());
    return this.getAuthenticationManager().authenticate(authRequest);
  }


  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;

  }

  @Override
  public void afterPropertiesSet() {
    Assert.notNull(getAuthenticationManager(), "authenticationManager must be specified");
    postProcess();
  }

}
