package org.chiwooplatform.samples.dao.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.chiwooplatform.security.authentication.AuthenticationUser;

/**
 * Created by seonbo.shim on 2017-07-06.
 * http://docs.spring.io/spring-tokens/mongodb/docs/current/reference/html/#repository-query-keywords
 */
public interface AuthenticationRepository extends MongoRepository<AuthenticationUser, String> {

    AuthenticationUser findById(String username);
}
