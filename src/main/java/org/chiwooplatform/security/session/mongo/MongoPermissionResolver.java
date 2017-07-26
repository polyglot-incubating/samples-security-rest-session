package org.chiwooplatform.security.session.mongo;

import org.chiwooplatform.security.core.PermissionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MongoPermissionResolver implements PermissionResolver {

  private final transient Logger logger = LoggerFactory.getLogger(MongoPermissionResolver.class);

  public MongoPermissionResolver() {
    super();
  }

  @Override
  public boolean hasPermission(Object principal) {
    logger.debug("args: {}", principal);
    return false;
  }

}
