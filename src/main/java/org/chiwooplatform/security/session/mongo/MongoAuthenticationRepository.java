package org.chiwooplatform.security.session.mongo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.chiwooplatform.security.authentication.AuthenticationUser;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.WriteResult;

/**
 * https://github.com/spring-projects/spring-tokens-mongodb
 * https://github.com/spring-projects/spring-tokens-mongodb/blob/9d4d47f503791cc5c7a30cffaaea31f5f8caf870/spring-tokens-mongodb/src/test/java/org/springframework/tokens/mongodb/core/ReactiveMongoTemplateTests.java
 */
public class MongoAuthenticationRepository implements AuthenticationRepository<AuthenticationUser> {
  private final transient Logger logger =
      LoggerFactory.getLogger(MongoAuthenticationRepository.class);

  private final MongoTemplate mongoTemplate;

  private final String collectionName = "authenticationUsers";

  Query queryId(Object id) {
    return new Query(Criteria.where("_id").is(id));
  }

  @Autowired
  public MongoAuthenticationRepository(MongoTemplate mongoTemplate) {
    super();
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void add(AuthenticationUser model) {
    mongoTemplate.insert(model, collectionName);
  }

  @Override
  public void save(AuthenticationUser model) {
    final String id = model.getUsername();
    AuthenticationUser oldModel = findOne(id);
    if (oldModel != null) {
      // Query query = new Query(Criteria.where("_id").is(id).and(key))
      // mongoTemplate.remove(query, collectionName);
    }
    mongoTemplate.save(model, collectionName);
  }

  @Override
  public boolean exists(String id) {
    return mongoTemplate.exists(queryId(id), collectionName);
  }

  @Override
  public boolean remove(String id) {
    WriteResult result = mongoTemplate.remove(queryId(id), collectionName);
    return result.getN() == 1 ? true : false;
  }

  @Override
  public AuthenticationUser findOne(String id) {
    return mongoTemplate.findOne(queryId(id), AuthenticationUser.class, collectionName);
  }

  @Override
  public Collection<AuthenticationUser> findAll(Map<String, Object> param) {
    return null;
  }

  public <D> Collection<D> findDocuments(Query query, Class<D> clazz) {
    Collection<D> result = mongoTemplate.find(query, clazz, collectionName);
    logger.debug("findDocuments: {}", result);
    return result;
  }

  public <D> D findProjection(Query query, Class<D> clazz) {
    List<D> result = mongoTemplate.find(query, clazz, collectionName);
    if (result == null) {
      return null;
    }
    return result.get(0);
  }

  public boolean update(Query query, Update update) {
    WriteResult result = mongoTemplate.updateMulti(query, update, collectionName);
    return result.getN() == 1 ? true : false;
  }

  @Override
  public void cleanExpiresToken(AuthenticationUser model) {
    String principal = model.getUsername();
    AuthenticationUser result = findOne(principal);
    if (result != null) {
      final Long currentTimestamp = System.currentTimeMillis();
      Query query =
          new Query(Criteria.where("_id").is(principal).and("tokens.expires").lt(currentTimestamp));
      ProjectionToken projectionToken = findProjection(query, ProjectionToken.class);
      logger.debug("projectionToken: {}", projectionToken);

    }
  }


}
