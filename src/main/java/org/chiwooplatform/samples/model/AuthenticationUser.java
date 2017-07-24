package org.chiwooplatform.samples.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;
import lombok.ToString;

@SuppressWarnings("serial")
@Data
@ToString
@JsonRootName("authToken")
public class AuthenticationUser implements Serializable {

  private Integer id;

  private String username;

  private String token; /* token. It can be modified with new token */

  /* UTC Timestamp "2014-01-31T15:30:58Z", It can be modified with new token */
  private Long expires;

  private Collection<GrantedAuthority> authorities;

  private Collection<UserToken> tokens;


  public Collection<UserToken> activeTokens() {
    if (this.tokens != null) {
      final List<UserToken> tokens = this.tokens.stream()
          .filter((v) -> DateUtils.isExpired(v.getExpires())).collect(Collectors.toList());
      return tokens;
    }
    return this.tokens;
  }

  public void authentication(final String token, final Long expires) {
    if (!StringUtils.isEmpty(token)) {
      if (this.tokens == null) {
        this.tokens = new HashSet<>();
      }
      final UserToken ut = new UserToken(token, expires);
      tokens.add(ut);
    }
  }

  public void authentication(final Authentication authentication) {
    if (authentication instanceof RestAuthenticationToken) {
      RestAuthenticationToken auth = (RestAuthenticationToken) authentication;
      final String token = auth.getToken();
      final Long expires = auth.getExpires();
      authentication(token, expires);
    }
  }
}
