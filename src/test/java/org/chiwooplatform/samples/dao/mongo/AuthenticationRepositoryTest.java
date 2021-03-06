package org.chiwooplatform.samples.dao.mongo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.security.AbstractMongoTests;
import org.chiwooplatform.security.authentication.AuthenticationUser;
import org.chiwooplatform.security.authentication.SimpleToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.extern.slf4j.Slf4j;

@ActiveProfiles("home")
@Slf4j
@RunWith(SpringRunner.class)
@EnableMongoRepositories("org.chiwooplatform.samples.dao.mongo")
@SpringBootTest(classes = AbstractMongoTests.class)
public class AuthenticationRepositoryTest {
    @Autowired
    private AuthenticationRepository repository;

    @Before
    public void init() {
        log.info("repository: {}", repository);
    }

    @Test
    public void testObject() throws Exception {
    }

    @Test
    public void testCRUD() throws Exception {
        log.info("repository: {}", repository);
        List<AuthenticationUser> tokens = repository.findAll();
        tokens.stream().forEach((v) -> {
            log.info("token: {}", v);
        });
        log.info("-------------------------------------------");
        String token = UUIDGenerator.uuid();
        Long expires = DateUtils.timeMillis(DateUtils.plusMins(1));
        AuthenticationUser at = new AuthenticationUser();
        at.setUserId(1001);
        at.setId("abc@abc");
        at.addToken(new SimpleToken(token, expires));
        at.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_ADM_1", "ROLE_ADM_2").stream()
                .map(v -> v.getAuthority()).collect(Collectors.toList()));
        if (repository.exists(at.getId())) {
            log.info("exists");
            AuthenticationUser oldAt = repository.findOne(at.getId());
            oldAt.newUser(Arrays.asList(new SimpleToken(UUIDGenerator.uuid(), System.currentTimeMillis())));
            repository.save(at);
        }
        else {
            log.info("not exists");
            repository.insert(at);
        }
        tokens = repository.findAll();
        tokens.stream().forEach((v) -> {
            log.info("token: {}", v);
        });
    }
}
