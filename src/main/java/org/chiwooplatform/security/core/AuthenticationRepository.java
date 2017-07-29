package org.chiwooplatform.security.core;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Query;

/**
 * @param <T> domain model class
 */
public interface AuthenticationRepository<T> {

    String COMPONENT_NAME = "securityAuthenticationRepository";
    String SECURITY_MONGO_COLLECTION_NAME = "authenticationUser";

    void add(T model);

    T findOne(String principal);

    List<T> findAll(Map<String, Object> param);

    public <D> List<D> findQuery(Query query, Class<D> clazz);

    public <D> D findProjection(Query query, Class<D> clazz);

    boolean exists(Query query);

    void save(T model);

    void saveOrUpdate(T model);

    boolean remove(String id);

    void clearExpiredTokens(String id);

    // @FunctionalInterface
    // interface QueryId<T, R extends Query> {
    // R queryID(T id);
    // }
    //
    // {
    // return new Query(Criteria.where("id").is(id));

}
