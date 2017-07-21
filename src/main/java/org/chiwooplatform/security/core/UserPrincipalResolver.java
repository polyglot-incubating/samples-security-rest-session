package org.chiwooplatform.security.core;

public interface UserPrincipalResolver {

  UserPrincipal getUser(Object args);
}
