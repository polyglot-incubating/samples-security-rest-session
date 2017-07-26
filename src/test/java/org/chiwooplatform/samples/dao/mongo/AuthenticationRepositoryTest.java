package org.chiwooplatform.samples.dao.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.junit4.SpringRunner;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.samples.model.AuthenticationUser;
import org.chiwooplatform.security.AbstractMongoTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AbstractMongoTests.class)
public class AuthenticationRepositoryTest {

  @Autowired
  private AuthenticationRepository repository;

  @Before
  public void init() {
    log.info("repository: {}", repository);
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
    at.setUsername("abc@abc");
    at.setToken(token);
    at.setExpires(expires);
    at.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_ADM_1", "ROLE_ADM_2"));
    at.authentication(token, expires);

    if (repository.exists(at.getUsername())) {
      log.info("exists");
      AuthenticationUser oldAt = repository.findOne(at.getUsername());
      oldAt.authentication(token, expires);
      log.info("oldAt.tokens: {}, oldAt.activeTokens(): {}", oldAt.getTokens().size(),
          oldAt.activeTokens().size());
      at.setTokens(oldAt.activeTokens());
      repository.save(at);
    } else {
      log.info("not exists");
      repository.insert(at);
    }


    tokens = repository.findAll();
    tokens.stream().forEach((v) -> {
      log.info("token: {}", v);
    });
  }

}
