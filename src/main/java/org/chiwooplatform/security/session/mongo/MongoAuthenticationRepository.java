package org.chiwooplatform.security.session.mongo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.chiwooplatform.security.authentication.AuthenticationUser;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
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

    private final String collectionName = AuthenticationRepository.SECURITY_MONGO_COLLECTION_NAME;

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
    public AuthenticationUser findOne(String id) {
        return mongoTemplate.findOne(queryId(id), AuthenticationUser.class,
                collectionName);
    }

    @Override
    public List<AuthenticationUser> findAll(Map<String, Object> param) {
        return null;
    }

    @Override
    public boolean exists(Query query) {
        return mongoTemplate.exists(query, collectionName);
    }

    public <D> List<D> findQuery(Query query, Class<D> clazz) {
        List<D> result = mongoTemplate.find(query, clazz, collectionName);
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
    public void save(AuthenticationUser model) {
        mongoTemplate.save(model, collectionName);
    }

    @Override
    public void saveOrUpdate(AuthenticationUser model) {
        final AuthenticationUser oldUser = findOne(model.getId());
        if (oldUser == null) {
            add(model);
        }
        else {
            final AuthenticationUser newUser = oldUser.newUser(model.getTokens());
            mongoTemplate.save(newUser, collectionName);
        }
    }

    @Override
    public boolean remove(String id) {
        WriteResult result = mongoTemplate.remove(queryId(id), collectionName);
        return result.getN() == 1 ? true : false;
    }

    @Override
    public void clearExpiredTokens(final String id) {
        final AuthenticationUser user = findOne(id);
        if (user == null || user.getTokens() == null) {
            return;
        }

        final long currentTimestamp = System.currentTimeMillis();

        List<Object> tokens = user.getTokens().stream()
                .filter((v) -> v.getExpires() < currentTimestamp).map((v) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("token", v.getToken());
                    map.put("expires", v.getExpires());
                    return new BasicDBObject(map);
                }).collect(Collectors.toList());
        if (!tokens.isEmpty()) {
            Query query = new Query(Criteria.where("_id").is(id));
            final Update update = new Update();
            update.pullAll("tokens", tokens.toArray());
            /*
             * 단건을 삭제할 경우엔 update.pull("tokens", new BasicDBObject("token", "3262c85d-cef2-41e7-988e-e8c0df44ee02"));
             * 여러건을 삭제할 경우 json 데이타 구성을 맞춰 줘야 한다. 문제는 일반적인 PoJo 의 경우 json 마샬/언마샬 오류가 나온다. Map 은 기본으로 지원 하는 듯 하다.
             */
            logger.debug("update.getUpdateObject(): {}", update.getUpdateObject());
            WriteResult wr = mongoTemplate.upsert(query, update, collectionName);
            logger.debug("getUpsertedId {}", wr);
        }
    }

}
