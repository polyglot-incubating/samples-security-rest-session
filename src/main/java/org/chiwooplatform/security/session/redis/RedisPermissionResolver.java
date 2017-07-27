package org.chiwooplatform.security.session.redis;

import java.util.Map;

import org.springframework.security.core.session.SessionInformation;

import org.chiwooplatform.context.Constants;
import org.chiwooplatform.security.core.PermissionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisPermissionResolver implements PermissionResolver {

  private final transient Logger logger = LoggerFactory.getLogger(RedisPermissionResolver.class);

  final RedisBackedSessionRegistry registry;

  public RedisPermissionResolver(RedisBackedSessionRegistry registry) {
    this.registry = registry;
  }

  @Override
  public boolean hasPermission(Map<String, Object> args) {
    logger.debug("args: {}", args);
    // final String principal = (String) args.get(Constants.PRINCIPAL);
    // final String permCd = (String) args.get(Constants.PERM_CODE);
    final String token = (String) args.get(Constants.TOKEN);

    SessionInformation session = registry.getSessionInformation((String) token);
    if (session == null || session.isExpired()) {
      return false;
    }
    return false;
  }

}
