package org.chiwooplatform.security.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import org.chiwooplatform.security.core.UserPrincipal;
import org.chiwooplatform.security.core.UserPrincipalResolver;

public class JdbcUserPrincipalResolver implements UserPrincipalResolver {

  private static final String SQL =
      "SELECT u.id, u.username, u.password, u.enabled FROM USER u WHERE u.username = ?";

  private final JdbcTemplate template;

  public JdbcUserPrincipalResolver(@Autowired JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public UserPrincipal getUser(Object args) {
    return template.queryForObject(SQL, new Object[] {args}, (rs, num) -> {
      final Integer id = rs.getInt("id");
      final String username = rs.getString("username");
      final String password = rs.getString("password");
      final Boolean enabled = rs.getBoolean("enabled");
      UserPrincipal user = new UserPrincipal(id, username, password);
      user.setEnabled(enabled);
      return user;
    });
  }
}
