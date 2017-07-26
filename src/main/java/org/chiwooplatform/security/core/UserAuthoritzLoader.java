package org.chiwooplatform.security.core;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public interface UserAuthoritzLoader {

  String COMPONENT_NAME = "userAuthoritzLoader";

  Collection<GrantedAuthority> loadUserAuthorities(Object principal);
}
