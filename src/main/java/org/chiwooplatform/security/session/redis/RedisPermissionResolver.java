package org.chiwooplatform.security.session.redis;

import org.springframework.security.core.session.SessionInformation;

import org.chiwooplatform.security.core.PermissionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisPermissionResolver implements PermissionResolver {

  private final transient Logger logger =
      LoggerFactory.getLogger(RedisPermissionResolver.class);

  final RedisBackedSessionRegistry registry;

  public RedisPermissionResolver(RedisBackedSessionRegistry registry) {
    this.registry = registry;
  }


  @Override
  public boolean hasPermission(Object token) {
    logger.debug("token: {}", token);

    SessionInformation session = registry.getSessionInformation((String) token);
    if (session == null || session.isExpired()) {
      return false;
    }
    return false;
  }

}
