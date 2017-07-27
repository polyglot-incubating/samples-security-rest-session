package org.chiwooplatform.security.session.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.security.authentication.AuthenticationUser;
import org.chiwooplatform.security.authentication.SimpleToken;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

/**
 * https://github.com/spring-projects/spring-tokens-mongodb
 * https://github.com/spring-projects/spring-tokens-mongodb/blob/9d4d47f503791cc5c7a30cffaaea31f5f8caf870/spring-tokens-mongodb/src/test/java/org/springframework/tokens/mongodb/core/ReactiveMongoTemplateTests.java
 */
public class MongoAuthenticationRepository
        implements AuthenticationRepository<AuthenticationUser> {
    private final transient Logger logger = LoggerFactory
            .getLogger(MongoAuthenticationRepository.class);

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

    private List<Object> getExpiredTokens(AuthenticationUser model, boolean onlyKey) {
        if (model == null || model.getTokens() == null) {
            return null;
        }
        List<Object> tokens = new ArrayList<>();
        model.getTokens().stream().forEach((v) -> {
            long expires = (Long) v.getExpires();
            if (DateUtils.isExpired(expires)) {
                if (onlyKey) {
                    tokens.add(v.getToken());
                }
                else {
                    tokens.add(v);
                }

            }
        });
        return tokens;
    }

    @Override
    public void save(AuthenticationUser model) {
        // final String id = model.getUsername();
        // AuthenticationUser oldModel = findOne(id);
        // if (oldModel != null) {
        // // Query query = new Query(Criteria.where("_id").is(id).and(key))
        // // mongoTemplate.remove(query, collectionName);
        // }
        final String id = model.getId();
        final AuthenticationUser user = findOne(id);
        if (user == null) {
            add(model);
        }
        else {
            final Collection<Object> expiredKeys = getExpiredTokens(user, true);

            // remove keys
            Query query = new Query(Criteria.where("_id").is(id));
            final Update update = new Update();
            Collection<Object> expiredTokens = getExpiredTokens(user, false);
            if (expiredTokens != null && !expiredKeys.isEmpty()) {
                List arr = new ArrayList<>();
                for (final Object etkn : expiredTokens) {
                    logger.debug("delete-token: {}", etkn);
                    SimpleToken stkn = (SimpleToken) etkn;
                    HashMap<String, Object> field = new HashMap<>();
                    field.put("token", stkn.getToken());
                    field.put("expires", stkn.getExpires());
                    arr.add(field);
                }
                update.pullAll("tokens", arr.toArray());
                WriteResult wr = mongoTemplate.upsert(query, update, collectionName);
                logger.debug("getUpsertedId {}", wr);
            }

            final Update add = new Update();
            for (SimpleToken token : model.getTokens()) {
                logger.debug("added-token: {}", token);
                HashMap<String, Object> field = new HashMap<>();
                field.put("token", token.getToken());
                field.put("expires", token.getExpires());
                add.push("tokens", field);
            }
            WriteResult wr2 = mongoTemplate.upsert(query, add, collectionName);
            logger.debug("getUpsertedId {}", wr2);
        }
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
        return mongoTemplate.findOne(queryId(id), AuthenticationUser.class,
                collectionName);
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
        D result = mongoTemplate.findOne(query, clazz, collectionName);
        return result;
    }

    public boolean update(Query query, Update update) {
        WriteResult result = mongoTemplate.updateMulti(query, update, collectionName);
        return result.getN() == 1 ? true : false;
    }

    @Override
    public void cleanExpiresToken(AuthenticationUser model) {
        String principal = model.getId();
        AuthenticationUser result = findOne(principal);
        if (result != null) {
            final Long currentTimestamp = System.currentTimeMillis();
            Query query = new Query(Criteria.where("_id").is(principal)
                    .and("tokens.expires").lt(currentTimestamp));
            ProjectionToken projectionToken = findProjection(query,
                    ProjectionToken.class);
            logger.debug("projectionToken: {}", projectionToken);

        }
    }

}
