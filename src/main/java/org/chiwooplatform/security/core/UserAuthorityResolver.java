package org.chiwooplatform.security.core;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public interface UserAuthorityResolver {

    Collection<? extends GrantedAuthority> getAuthorities( Object args );
}
