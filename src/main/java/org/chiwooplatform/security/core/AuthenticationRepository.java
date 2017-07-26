package org.chiwooplatform.security.core;

import org.springframework.security.core.Authentication;

public interface AuthenticationRepository {
  
  String COMPONENT_NAME = "authenticationRepository";

  void save(Authentication authentication);
}
