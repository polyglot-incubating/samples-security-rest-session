package org.chiwooplatform.security.authentication;

import java.security.Principal;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import org.chiwooplatform.security.core.UserPrincipal;

@SuppressWarnings("serial")
public class RestAuthenticationToken extends AbstractAuthenticationToken {

  private String agent; // Client Agent (chrome, edge, ...)

  private final Object principal;

  private final Object credentials;

  private final String token;

  private Long expires;

  public RestAuthenticationToken(Object principal, Object credentials) {
    super(null);
    this.principal = principal;
    this.credentials = credentials;
    this.token = null;
    super.setAuthenticated(false);
  }

  /**
   * @param principal This is can be used to represent any entity, such as an individual, a
   *        corporation, and a login id.
   * @param credentials generally represent password.
   * @param token auth token
   */
  public RestAuthenticationToken(UserPrincipal principal, Object credentials, String token) {
    super(null);
    this.principal = principal.getUsername();
    this.credentials = credentials;
    this.token = token;
    this.setDetails(principal);
    super.setAuthenticated(true);
  }

  /**
   * @param principal This is can be used to represent any entity, such as an individual, a
   *        corporation, and a login id.
   * @param credentials generally represent password.
   * @param token auth token
   * @param authorities Collection of granted authorities.
   */
  public RestAuthenticationToken(Object principal, Object credentials, String token,
      String... authorities) {
    super(AuthorityUtils.createAuthorityList(authorities));
    // Arrays.asList( authorities ).stream().map( ( role ) -> new SimpleGrantedAuthority( role )
    // ).collect( Collectors.toList() )
    this.principal = principal;
    this.credentials = credentials;
    this.token = token;
    super.setAuthenticated(true);
  }

  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    if (isAuthenticated) {
      throw new IllegalArgumentException(
          "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
    }
    super.setAuthenticated(false);
  }

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  /**
   * @return the token
   */
  public String getToken() {
    return token;
  }

  public Long getExpires() {
    return expires;
  }

  public void setExpires(Long expires) {
    this.expires = expires;
  }

  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  @Override
  public Object getPrincipal() {
    return this.principal;
  }

  public String getName() {
    Object src = this.getPrincipal();
    if (src instanceof UserDetails) {
      return ((UserDetails) src).getUsername();
    }
    if (src instanceof Principal) {
      return ((Principal) src).getName();
    }
    return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
  }
}
