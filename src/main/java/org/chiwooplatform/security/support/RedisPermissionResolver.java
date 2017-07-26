package org.chiwooplatform.security.support;

import org.chiwooplatform.security.core.PermissionResolver;

public class RedisPermissionResolver implements PermissionResolver {

  @Override
  public boolean hasPermission(Object args) {
    return false;
  }

}
