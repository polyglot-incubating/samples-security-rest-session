package org.chiwooplatform.security.session.mongo;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;

import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.authentication.SimpleToken;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.chiwooplatform.security.core.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoAuthenticationRepository implements AuthenticationRepository {

  private final transient Logger logger =
      LoggerFactory.getLogger(MongoAuthenticationRepository.class);

  private final MongoTemplate mongoTemplate;

  private final String collectionName = "Authentications";

  @Autowired
  public MongoAuthenticationRepository(MongoTemplate mongoTemplate) {
    super();
    this.mongoTemplate = mongoTemplate;
  }



  @Override
  public void save(Authentication authentication) {
    logger.debug("authentication: {}", authentication);
    if (authentication == null || !(authentication instanceof RestAuthenticationToken)) {
      logger.info("authentication type is '{}'.", authentication.getClass().getName());
      return;
    }

    RestAuthenticationToken authenticationToken = (RestAuthenticationToken) authentication;
    final String principal = authenticationToken.getPrincipal().toString();
    final String token = authenticationToken.getToken();
    final Long expires = authenticationToken.getExpires();

    try {
      UserProfile user = (UserProfile) authenticationToken.getDetails();
      if (user == null) {
        logger.warn("UserDetails is null.");
        return;
      }
      logger.warn("UserDetails: {}", user);
      Collection<String> authorities = null;
      if (user.getAuthorities() != null) {
        authorities = user.getAuthorities().stream().map((v) -> v.getAuthority())
            .collect(Collectors.toList());
      }
      Query findQuery = new Query(Criteria.where("username").is(principal));
      boolean exists = mongoTemplate.exists(findQuery, AuthenticationUser.class, collectionName);
      logger.debug("exists: {}", exists);
      if (!exists) {
        AuthenticationUser userSession = new AuthenticationUser();
        userSession.setAuthorities(authorities);
        userSession.setUsername(principal);
        userSession.setUserId(user.getId());
        userSession.authentication(token, expires);
        logger.debug("userSession: {}", userSession);
        mongoTemplate.save(userSession, collectionName);
      } else {
        AuthenticationUser authUser =
            mongoTemplate.findOne(findQuery, AuthenticationUser.class, collectionName);
        authUser.authentication(token, expires);
        logger.debug("authUser: {}", authUser);
        Collection<SimpleToken> activeTokens = authUser.activeTokens();
        AuthenticationUser userSession = new AuthenticationUser(activeTokens);
        userSession.setAuthorities(authorities);
        userSession.setUsername(principal);
        userSession.setUserId(user.getId());
        logger.debug("userSession: {}", userSession);
        
        // Update update = new Update().inc("age", 1);
        
        // Person p = mongoTemplate.findAndModify(query, update, Person.class); // return's old person object
        // mongoTemplate.save(userSession, collectionName);
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

  }


}
