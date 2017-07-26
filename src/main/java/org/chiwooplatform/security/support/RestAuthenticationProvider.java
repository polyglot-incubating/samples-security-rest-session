package org.chiwooplatform.security.support;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.core.UserProfile;
import org.chiwooplatform.security.core.UserProfileResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Please use this filter for testing purposes only.
 * 
 * <pre>
 * &#64;Bean
 * public AuthenticationProvider authenticationProvider() {
 *   final RestAuthenticationProvider authenticationProvider =
 *       new RestAuthenticationProvider(userPrincipalResolver());
 *   return authenticationProvider;
 * }
 * </pre>
 */
public class RestAuthenticationProvider implements AuthenticationProvider {

  private final transient Logger logger = LoggerFactory.getLogger(RestAuthenticationProvider.class);

  private static final int EXPIRES_DAYS = 30;

  private final UserProfileResolver userProfileResolver;

  protected final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

  private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

  public RestAuthenticationProvider(UserProfileResolver userPrincipalResolver) {
    super();
    this.userProfileResolver = userPrincipalResolver;
  }

  public void setUserDetailsChecker(UserDetailsChecker userDetailsChecker) {
    this.userDetailsChecker = userDetailsChecker;
  }

  /**
   * @see AuthenticationProvider#authenticate(Authentication)
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    final String username = authentication.getName();
    try {
      RestAuthenticationToken authenticationToken = (RestAuthenticationToken) authentication;
      UserProfile user = userProfileResolver.getUser(username);
      userDetailsChecker.check(user);
      String credentials = authentication.getCredentials().toString();
      if (!credentials.equals(user.getPassword())) {
        throw new BadCredentialsException(messages.getMessage(
            "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
      }
      final String token = authenticationToken.getToken();
      user.setToken(token);
      if (user instanceof CredentialsContainer) {
        ((CredentialsContainer) user).eraseCredentials();
      }
      final RestAuthenticationToken newAuthentication = new RestAuthenticationToken(user);
      final Long expires = DateUtils.timeMillis(DateUtils.plusDays(EXPIRES_DAYS));
      newAuthentication.setExpires(expires);
      return newAuthentication;
    } catch (AuthenticationException ae) {
      logger.error("AE: {}", ae.getMessage());
      throw ae;
    } catch (RuntimeException re) {
      logger.error("RE: {}", re.getMessage());
      throw new UsernameNotFoundException(this.messages.getMessage("JdbcDaoImpl.notFound",
          new Object[] {username}, "Username {0} not found"));
    }
  }

  /**
   * @see AuthenticationProvider#supports(Class)
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return (RestAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
