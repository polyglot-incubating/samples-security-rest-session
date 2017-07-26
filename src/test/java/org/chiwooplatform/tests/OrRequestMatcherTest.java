package org.chiwooplatform.tests;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrRequestMatcherTest {


  private OrRequestMatcher orRequestMatcher(final String... urls) {
    final List<RequestMatcher> requestMatchers = Arrays.asList(urls).stream()
        .map(v -> new AntPathRequestMatcher(v)).collect(Collectors.toList());
    return new OrRequestMatcher(requestMatchers);
  }

  @Test
  public void testOrRequestMatcher() throws Exception {
    OrRequestMatcher matchers = orRequestMatcher("/abc/**", "/abd/**");
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/ABC/test");
    log.info("getRequestURI: {}", req.getRequestURI());
    log.info("matchers.matches: {}", matchers.matches(req));
    matchers = orRequestMatcher("/ABC/**", "/ABC/test");
    log.info("matchers.matches: {}", matchers.matches(req));

  }
}
