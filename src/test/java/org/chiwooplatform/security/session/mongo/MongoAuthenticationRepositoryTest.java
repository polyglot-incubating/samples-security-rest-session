package org.chiwooplatform.security.session.mongo;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Exp;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.chiwooplatform.context.support.ConverterUtils;
import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.security.AbstractMongoTests;
import org.chiwooplatform.security.authentication.AuthenticationUser;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.authentication.SimpleToken;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.chiwooplatform.security.core.UserProfile;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = { /*"home", */"default" })
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractMongoTests.class,
        MongoAuthenticationRepositoryTest.MongoConfiguration.class })
public class MongoAuthenticationRepositoryTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Configuration
    static class MongoConfiguration {
        @Bean
        public AuthenticationRepository<AuthenticationUser> mongoAuthenticationRepository(
                MongoTemplate mongoTemplate) {
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
    }

    UserProfile user() {
        UserProfile user = new UserProfile(761120, "lamp@gmail.com", null);
        user.setToken("ce2b0fe8-3631-4182-8052-c317e29cbfd2");
        user.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_ADMIN",
                "ROLE_MANAGER", "ROLE_USER"));
        return user;
    }

    Collection<String> authorities(UserProfile user) {
        Collection<String> authorities = null;
        if (user.getAuthorities() != null) {
            authorities = user.getAuthorities().stream().map((v) -> v.getAuthority())
                    .collect(Collectors.toList());
        }
        return authorities;
    }

    AuthenticationUser model(RestAuthenticationToken authentication) {
        if (authentication == null) {
            return null;
        }
        authentication.setExpires(DateUtils.timeMillis(DateUtils.plusMins(1)));
        AuthenticationUser model = new AuthenticationUser();
        model.setId(authentication.getPrincipal().toString());
        model.authentication(authentication.getToken(), authentication.getExpires());
        UserProfile user;
        if (authentication.getDetails() != null) {
            user = (UserProfile) authentication.getDetails();
            if (user != null) {
                model.setAuthorities(authorities(user));
                model.setUserId(user.getId());
            }
        }
        return model;
    }

    @Test
    public void ut1001_add() throws Exception {
        UserProfile user = user();
        RestAuthenticationToken authentication = new RestAuthenticationToken(user);
        AuthenticationUser model = model(authentication);
        authenticationRepository.add(model);
    }

    @Test
    public void ut1002_exists() throws Exception {
        UserProfile user = user();
        boolean exists = authenticationRepository.exists(user.getUsername());
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
        AuthenticationUser oldUser = authenticationRepository.findOne(user.getUsername());
        final String token = UUIDGenerator.uuid();
        final Long expires = DateUtils.timeMillis(DateUtils.plusMins(1));
        AuthenticationUser newUser = oldUser.newUser();
        newUser.authentication(token, expires);
        log.debug("New AuthenticationUser: {}", newUser);
        authenticationRepository.save(newUser);
    }

    @Test
    public void ut1006_saveOrUpdate() throws Exception {
        UserProfile user = user();
        AuthenticationUser oldUser = authenticationRepository.findOne(user.getUsername());
        final String token = UUIDGenerator.uuid();
        final Long expires = DateUtils.timeMillis(DateUtils.plusMins(1));
        AuthenticationUser newUser = oldUser.newUser();
        newUser.authentication(token, expires);
        log.debug("New AuthenticationUser: {}", newUser);
        authenticationRepository.saveOrUpdate(newUser);
    }

    
    @Test
    public void ut1006_findDocuments() throws Exception {
        UserProfile user = user();
        Query query = new Query(Criteria.where("_id").is(user.getUsername()));
        query.fields().include("tokens");
        log.info("query: {}", query.toString());
        Collection<AuthenticationUser> result = authenticationRepository
                .findQuery(query, AuthenticationUser.class);
        log.info("result: {}", result);
    }

    @Test
    public void ut1007_findProjection() throws Exception {
        UserProfile user = user();
        Query query = new Query(Criteria.where("_id").is(user.getUsername()));
        query.fields().include("tokens");
        query.getQueryObject().containsField("token");
        log.info("query: {}", query.toString());
        AuthenticationUser result = authenticationRepository.findProjection(query,
                AuthenticationUser.class);
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
        ProjectionToken result = authenticationRepository.findProjection(query,
                ProjectionToken.class);
        log.info("result: {}", result);
        if (result != null && result.getTokens() != null) {
            result.getTokens().stream().forEach((v) -> {
                long expires = (Long) v.getExpires();
                if (DateUtils.isExpired(expires)) {
                    log.info("token '{}' is expired due-date. {}", v.getToken(),
                            DateUtils.getFormattedString(expires));
                }
                else {
                    log.info("token '{}' is alived {}", v.getToken(),
                            DateUtils.getFormattedString(expires));
                }
            });
        }

    }

}
