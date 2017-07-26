package org.chiwooplatform.security.support;

import java.util.Collection;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import org.chiwooplatform.security.core.UserAuthoritzLoader;
import org.chiwooplatform.security.core.UserProfile;
import org.chiwooplatform.security.core.UserProfileResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcUserProfileResolver implements UserProfileResolver, InitializingBean {

  private final Logger logger = LoggerFactory.getLogger(JdbcUserProfileResolver.class);

  private static final String SQL =
      "SELECT u.id, u.username, u.password, u.enabled FROM USER u WHERE u.username = ?";

  private final JdbcTemplate jdbcTemplate;

  private UserAuthoritzLoader userAuthoritzLoader;

  @Autowired
  public JdbcUserProfileResolver(JdbcTemplate jdbcTemplate) {
    super();
    this.jdbcTemplate = jdbcTemplate;
  }

  public void setUserAuthoritzLoader(UserAuthoritzLoader userAuthoritzLoader) {
    this.userAuthoritzLoader = userAuthoritzLoader;
  }

  @Override
  public UserProfile getUser(Object args) {
    UserProfile user = jdbcTemplate.queryForObject(SQL, new Object[] {args}, (rs, num) -> {
      final Integer id = rs.getInt("id");
      final String username = rs.getString("username");
      final String password = rs.getString("password");
      final Boolean enabled = rs.getBoolean("enabled");
      UserProfile u = new UserProfile(id, username, password);
      u.setEnabled(enabled);
      return u;
    });
    logger.debug("userAuthoritzLoader: {}", userAuthoritzLoader);
    if (userAuthoritzLoader != null) {
      Collection<GrantedAuthority> authorities =
          userAuthoritzLoader.loadUserAuthorities(user.getUsername());
      user.setAuthorities(authorities);
    }
    return user;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (this.userAuthoritzLoader == null) {
      logger.warn("Can not load authorities of user because not found UserAuthoritzLoader.");
    }
    Assert.notNull(jdbcTemplate, "JdbcTemplate must be specified");
  }
}
