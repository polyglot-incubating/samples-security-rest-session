package org.chiwooplatform.security.core;

import java.util.Collection;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Query;

/**
 * @param <T> domain model class
 */
public interface AuthenticationRepository<T> {

  String COMPONENT_NAME = "authenticationRepository";

  void add(T model);

  void save(T model);

  boolean exists(String id);

  boolean remove(String id);

  T findOne(String principal);

  Collection<T> findAll(Map<String, Object> param);

  public <D> Collection<D> findDocuments(Query query, Class<D> clazz);

  public <D> D findProjection(Query query, Class<D> clazz);

  void cleanExpiresToken(T model);

  // @FunctionalInterface
  // interface QueryId<T, R extends Query> {
  // R queryID(T id);
  // }
  //
  // {
  // return new Query(Criteria.where("id").is(id));


}
