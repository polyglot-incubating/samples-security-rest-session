package org.chiwooplatform.security.session.mongo;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.security.AbstractMongoTests;
import org.chiwooplatform.security.authentication.AuthenticationUser;
import org.chiwooplatform.security.authentication.SimpleToken;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.chiwooplatform.security.core.UserProfile;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = { "home", /* "default" */ })
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractMongoTests.class, MongoAuthenticationRepositoryTest.MongoConfiguration.class })
public class MongoAuthenticationRepositoryTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @EnableMongoRepositories
    @Configuration
    static class MongoConfiguration {
        @Bean
        public AuthenticationRepository<AuthenticationUser> mongoAuthenticationRepository(MongoTemplate mongoTemplate) {
            log.info("mongoTemplate: {}", mongoTemplate);
            return new MongoAuthenticationRepository(mongoTemplate);
        }
        // @Bean
        // public String stringSample( MongoTemplate mongoTemplate )
        // {
        // log.info( "mongoTemplate: {}", mongoTemplate );
        // return new String( "stringSample" );
        // }
    }

    @Autowired
    private MongoAuthenticationRepository authenticationRepository;

    @Test
    public void testObjects() throws Exception {
        log.info("mongoTemplate: {}", mongoTemplate);
        log.info("authenticationRepository: {}", authenticationRepository);
    }

    @Test
    public void testName() throws Exception {
        log.info("{}", System.currentTimeMillis());
        log.info("A {}", DateUtils.getFormattedString(1501153277215L));
        log.info("B {}", DateUtils.getFormattedString(1501156917036L));
        log.info("C {}", DateUtils.getFormattedString(1501157173852L));
        log.info("C {}", DateUtils.timeMillis(DateUtils.plusMins(60)));
    }

    UserProfile user() {
        UserProfile user = new UserProfile(761120, "lamp@gmail.com", null);
        user.setToken("ce2b0fe8-3631-4182-8052-c317e29cbfd2");
        user.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER"));
        return user;
    }

    Collection<String> authorities(UserProfile user) {
        Collection<String> authorities = null;
        if (user.getAuthorities() != null) {
            authorities = user.getAuthorities().stream().map((v) -> v.getAuthority()).collect(Collectors.toList());
        }
        return authorities;
    }

    AuthenticationUser model(UserProfile user) {
        final String token = UUIDGenerator.uuid();
        final long expires = DateUtils.timeMillis(DateUtils.plusMins(1));
        return model(user, token, expires);
    }

    AuthenticationUser model(UserProfile user, final String token, final Long expires) {
        final SimpleToken simpleToken = new SimpleToken(token, expires);
        AuthenticationUser model = new AuthenticationUser();
        model.setId(user.getUsername());
        model.addToken(simpleToken);
        model.setAuthorities(authorities(user));
        model.setUserId(user.getId());
        return model;
    }

    @Test
    public void ut1001_add() throws Exception {
        UserProfile user = user();
        AuthenticationUser model = model(user);
        authenticationRepository.add(model);
    }

    @Test
    public void ut1002_exists() throws Exception {
        UserProfile user = user();
        Query query = Query.query(Criteria.where("_id").is(user.getUsername()));
        boolean exists = authenticationRepository.exists(query);
        log.info("exists: {}", exists);
    }

    @Test
    public void ut1003_findOne() throws Exception {
        UserProfile user = user();
        AuthenticationUser model = authenticationRepository.findOne(user.getUsername());
        log.info("AuthenticationUser: {}", model);
    }

    @Test
    public void ut1004_remove() throws Exception {
        UserProfile user = user();
        boolean result = authenticationRepository.remove(user.getUsername());
        log.info("result: {}", result);
    }

    @Test
    public void ut1005_save() throws Exception {
        UserProfile user = user();
        AuthenticationUser model = authenticationRepository.findOne(user.getUsername());
        model.getAuthorities().add("ROLE_TEST");
        authenticationRepository.save(model);
    }

    @Test
    public void ut1006_saveOrUpdate() throws Exception {
        UserProfile user = user();
        AuthenticationUser model = model(user);
        authenticationRepository.saveOrUpdate(model);
    }

    @Test
    public void ut1006_findDocuments() throws Exception {
        UserProfile user = user();
        Query query = new Query(Criteria.where("_id").is(user.getUsername()));
        query.fields().include("tokens");
        log.info("query: {}", query.toString());
        Collection<AuthenticationUser> result = authenticationRepository.findQuery(query, AuthenticationUser.class);
        log.info("result: {}", result);
    }

    @Test
    public void ut1007_findProjection() throws Exception {
        UserProfile user = user();
        Query query = new Query(Criteria.where("_id").is(user.getUsername()));
        query.fields().include("tokens");
        query.getQueryObject().containsField("token");
        log.info("query: {}", query.toString());
        AuthenticationUser result = authenticationRepository.findProjection(query, AuthenticationUser.class);
        log.info("result: {}", result);
    }

    @Test
    public void ut1008_findTokenExpires() throws Exception {
        UserProfile user = user();
        Query query = new Query(Criteria.where("_id").is(user.getUsername())
        // .and("tokens.expires").lt(System.currentTimeMillis())
        );
        query.fields().include("tokens");
        log.info("query: {}", query);
        ProjectionToken result = authenticationRepository.findProjection(query, ProjectionToken.class);
        log.info("result: {}", result);
        if (result != null && result.getTokens() != null) {
            result.getTokens().stream().forEach((v) -> {
                long expires = (Long) v.getExpires();
                if (DateUtils.isExpired(expires)) {
                    log.info("token '{}' is expired due-date. {}", v.getToken(), DateUtils.getFormattedString(expires));
                }
                else {
                    log.info("token '{}' is alived {}", v.getToken(), DateUtils.getFormattedString(expires));
                }
            });
        }
    }

    @Test
    public void ut1009_clearExpiredTokens() throws Exception {
        UserProfile user = user();
        authenticationRepository.clearExpiredTokens(user.getUsername());
    }

    @Test
    public void ut1010_hasPermission() throws Exception {
        UserProfile user = user();
        final String id = user.getUsername();
        final String permCd = "ROLE_ADMIN";
        final String //
        // token = "042d7edc-3cff-42e9-9c54-49c0c7b786de";
        token = "ebad436e-38b6-44da-80fd-47cf03d211bb";
        final Long expires = System.currentTimeMillis();

        /**
         * <pre>
         * <code>
         * select *
         * from   authenticationUser
         * where  _id = 'username'
         * and    authorities = 'ROLE_ADMIN' -- contains ( ROLE_ADMIN )
         * and    tokens.token = '042d7edc-3cff-42e9-9c54-49c0c7b786de'
         * and    tokens.expires >= CURRENT_TIMESTAMP
         * </code>
         * </pre>
         */
        Query sql = new Query(Criteria.where("_id").is(id).and("authorities").is(permCd)
                .andOperator(Criteria.where("tokens.token").is(token).and("tokens.expires").gte(expires)));
        boolean perm = authenticationRepository.exists(sql);
        log.info(sql.toString());
        log.info("permission1: {}", perm);

        /**
         * <pre>
         * <code>
         * select *
         * from   authenticationUser
         * where  _id = 'username'
         * and    authorities = 'ROLE_ADMIN' -- contains ( ROLE_ADMIN )
         * and    tokens = null 
         *      (
         *          and   token = '042d7edc-3cff-42e9-9c54-49c0c7b786de' 
         *          and   expires >= CURRENT_TIMESTAMP
         *      )   
         * </code>
         * </pre>
         */
        Query sql2 = new Query(Criteria.where("_id").is(id).and("authorities").is(permCd)
                /* case 1 */
                // .and("tokens")
                /* case 2 */
                .and("tokens").andOperator(Criteria.where("token").is(token).and("expires").gte(expires))

        );
        boolean perm2 = authenticationRepository.exists(sql2);
        log.info(sql2.toString());
        log.info("permission2: {}", perm2);

        /**
         * <pre>
         * <code>
         * select *
         * from   authenticationUser
         * where  _id = 'username'
         * and    authorities = 'ROLE_ADMIN' -- contains ( ROLE_ADMIN )
         * and    tokens IN (
         *                    token = '042d7edc-3cff-42e9-9c54-49c0c7b786de'
         *              and   expires >= CURRENT_TIMESTAMP
         *            )
         * </code>
         * 
         * MongoDB 쿼리는 아래와 같다.
         * <code>
         * db.authenticationUser.find(
         *     {"_id":"lamp.java@gmail.com",
         *       "authorities": "API_ComCode.get",
         *       "tokens": {
         *         $elemMatch: {
         *           "token": "ff3c045c-d460-4587-b1fb-5d1351a58c9a",
         *           "expires": { $gte: 1501325034067 }
         *         }   
         *       }
         *     }
         * )
         * </code>
         * </pre>
         */
        Query query = new Query(Criteria.where("_id").is(id).and("authorities").is(permCd).and("tokens")
                .elemMatch(Criteria.where("token").is(token).and("expires").gte(expires)));
        boolean hasPermission = authenticationRepository.exists(query);
        log.info(query.toString());
        log.info("hasPermission: {}", hasPermission);
    }

}
