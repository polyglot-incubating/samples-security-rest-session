package org.chiwooplatform.security.session.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.test.context.junit4.SpringRunner;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.security.AbstractRedisTests;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AbstractRedisTests.class)
public class RedisBackedSessionRegistryTest {

  @Autowired
  private RedisBackedSessionRegistry registry;

  @Test
  public void testInstances() throws Exception {
    log.info("registry: {}", registry);
  }

  @Test
  public void testToken() throws Exception {
    String token = "1f7e5964-49b9-4847-aae2-13fbd9b6c0aa";
    String principal = "lamp.java@gmail.com";

    SessionInformation session = registry.getSessionInformation(token);
    log.info("session: {}", session);
    log.info("session.getPrincipal(): {}", session.getPrincipal());
    log.info("session.isExpired(): {}", session.isExpired());
    log.info("session.getLastRequest(): {}",
        DateUtils.getFormattedString(session.getLastRequest(), DateUtils.LIST_TIMESTAMP_FORMAT));
    log.info("session.getLastRequest(): {}", session.getPrincipal().getClass().getName());

    List<SessionInformation> sessions = registry.getAllSessions(principal, false);
    log.info("withPrincipal: {}", sessions);
  }

}
