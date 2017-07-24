package org.chiwooplatform.samples.dao.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.chiwooplatform.samples.model.AuthenticationUser;

/**
 * Created by seonbo.shim on 2017-07-06.
 * http://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#repository-query-keywords
 */
public interface AuthenticationRepository extends MongoRepository<AuthenticationUser, Integer> {

  AuthenticationUser findByUsername(String username);

  AuthenticationUser findByToken(String token);

  List<AuthenticationUser> findByUsernameLike(String username);
}
