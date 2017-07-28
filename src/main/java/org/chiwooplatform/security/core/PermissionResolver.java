package org.chiwooplatform.security.core;

import java.util.Map;

public interface PermissionResolver {

    boolean hasPermission(Map<String, Object> args);
}
