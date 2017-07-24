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
import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.core.UserPrincipal;
import org.chiwooplatform.security.core.UserPrincipalResolver;
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

  private final UserPrincipalResolver userPrincipalResolver;

  protected final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

  private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

  public RestAuthenticationProvider(UserPrincipalResolver userPrincipalResolver) {
    super();
    this.userPrincipalResolver = userPrincipalResolver;
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
      UserPrincipal userPrincipal = userPrincipalResolver.getUser(username);
      userDetailsChecker.check(userPrincipal);
      String credentials = authentication.getCredentials().toString();
      if (!credentials.equals(userPrincipal.getPassword())) {
        throw new BadCredentialsException(messages.getMessage(
            "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
      }
      if (userPrincipal instanceof CredentialsContainer) {
        ((CredentialsContainer) userPrincipal).eraseCredentials();
      }
      final String token = UUIDGenerator.uuid();
      RestAuthenticationToken newAuthentication =
          new RestAuthenticationToken(userPrincipal, "", token);
      final Long expires = DateUtils.timeMillis(DateUtils.plusDays(EXPIRES_DAYS));
      /*
       * final String expiresStringFormat =
       * DateUtils.getFormattedString(DateUtils.plusDays(EXPIRES_DAYS),
       * DateUtils.DEFAULT_TIMESTAMP_FORMAT);
       */
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
