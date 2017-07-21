package org.chiwooplatform.samples.dao.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.chiwooplatform.samples.model.AuthToken;

/**
 * Created by seonbo.shim on 2017-07-06.
 * http://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#repository-query-keywords
 */
public interface AuthenticationRepository
    extends MongoRepository<AuthToken, String> {

    AuthToken findByUsername( String username );

    AuthToken findByToken( String token );

    List<AuthToken> findByUsernameLike( String username );
}
