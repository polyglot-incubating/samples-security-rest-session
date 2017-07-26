package org.chiwooplatform.security.support;

import java.util.Collection;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import org.chiwooplatform.security.core.UserAuthoritzLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcUserAuthoritzLoader implements UserAuthoritzLoader, InitializingBean {


  private final Logger logger = LoggerFactory.getLogger(JdbcUserAuthoritzLoader.class);

  private JdbcTemplate jdbcTemplate;

  private static final String SQL = selectSql();

  private static String selectSql() {
    StringBuilder builder = new StringBuilder();
    builder.append("select  p.perm_cd ");
    builder.append('\n').append("from    USER u ");
    builder.append('\n').append("inner   join USER_PERM_REL p");
    builder.append('\n').append("where   u.username = ? ");
    builder.append('\n').append("and     enabled = 1 ");
    builder.append('\n').append("and     u.id = p.user_id ");
    return builder.toString();
  }

  public JdbcUserAuthoritzLoader() {
    super();
  }

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Collection<GrantedAuthority> loadUserAuthorities(Object principal) {
    logger.debug("principal: {}", principal);
    return jdbcTemplate.query(SQL, new Object[] {principal}, (rs, num) -> {
      GrantedAuthority ga = new SimpleGrantedAuthority(rs.getString("perm_cd"));
      return ga;
    });
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(jdbcTemplate, "JdbcTemplate must be specified");
  }

}
