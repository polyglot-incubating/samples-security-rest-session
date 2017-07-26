package org.chiwooplatform.security.core;

import java.util.Collection;

import org.chiwooplatform.security.session.mongo.AuthenticationUser;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;

public interface AuthenticationRepository
{
    String COMPONENT_NAME = "authenticationRepository";

    void add( Authentication authentication );

    void save( AuthenticationUser user );

    boolean exists( String principal );

    AuthenticationUser findOne( String principal );

    Collection<AuthenticationUser> findAll( Query query );
}
