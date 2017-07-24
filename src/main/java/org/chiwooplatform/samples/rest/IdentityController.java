package org.chiwooplatform.samples.rest;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.net.URI;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.chiwooplatform.samples.dao.mongo.AuthenticationRepository;
import org.chiwooplatform.samples.model.AuthenticationUser;
import org.chiwooplatform.samples.model.SimpleCredentials;
import org.chiwooplatform.samples.support.ConverterUtils;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.session.redis.RedisBackedSessionRegistry;
import org.chiwooplatform.web.support.WebUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class IdentityController {

  protected static final String BASE_URI = "/identity";

  private final AuthenticationRepository repository;

  @Autowired
  private AuthenticationManager authenticationManager;

  public IdentityController(@Autowired AuthenticationRepository repository,
      @Autowired NamedParameterJdbcTemplate jdbcTemplate) {
    this.repository = repository;
  }

  /**
   * <pre>
   * spring-session 주요 스키마 
   * 클라이언트 로그인 세션 아이디     - spring:session:sessions:{sessionId}
   * 클라이언트 로그인 세션 만료시간  - spring:session:sessions:expires:{sessionId}
   * 클라이언트 인증 principal  - spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:{principal}
   *  
   * TODO mongo db update 처리를 보완 하자.
   * 마스터키는 username
   * {
   *   username: xxx,
   *   expires: 1500545160000,
   *   sessions: [ "b062826d-605b-414a-b6ee-1d47c447e10d", "2825010a-9a17-4877-b1c2-8bb59551613c" ],
   *   permissions: [
   *      "API_COM_XXX", "API_COM_111"
   *   ]
   * }
   * </pre>
   * 
   * @param creds
   * @return
   */
  @PostMapping(value = BASE_URI + "/auth/tokens", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthenticationUser> authenticate(@RequestBody SimpleCredentials creds) {
    log.debug("{}", creds);
    AuthenticationUser authToken = new AuthenticationUser();
    Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
    log.debug("oldAuthentication: {}", oldAuthentication);
    if (oldAuthentication != null && oldAuthentication.isAuthenticated()) {
      log.debug("Found oldAuthentication: {}", oldAuthentication);
    }
    Authentication authentication =
        new RestAuthenticationToken(creds.getUsername(), creds.getPassword());
    try {
      final Authentication newAuthentication = authenticationManager.authenticate(authentication);
      log.debug("newAuthentication: {}", newAuthentication);
      log.debug("principal: {}", newAuthentication.getPrincipal());
      SecurityContextHolder.getContext().setAuthentication(newAuthentication);
      authToken.authentication(newAuthentication);
      // repository.save(arg0)
      final URI location = WebUtils.uriLocation("/{id}", newAuthentication.getName());
      return ResponseEntity.created(location).body(authToken);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      // return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( null );
      throw e;
    }
  }

  @Autowired
  private RedisBackedSessionRegistry sessionRegistry;

  @GetMapping(value = BASE_URI + "/active-users", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> getActiveUsers() {
    log.info("sessionRepository: {}", sessionRegistry);
    List<Object> principals = sessionRegistry.getAllPrincipals();
    return ResponseEntity.ok(principals);
  }

  // @RequestMapping(value = "/identity/auth/query", method = RequestMethod.GET, consumes = {
  // MediaType.APPLICATION_JSON_VALUE })
  // @ResponseStatus(HttpStatus.OK)
  // public @JsonSerialize List<AuthenticationUser> query( @RequestParam Map<String, Object> params,
  // @PageableDefault(sort = { "expires" }) Pageable pageable,
  // HttpSession session )
  // throws Exception {
  // final String sessionId = session.getId();
  // log.debug( "sessionId: {}", sessionId );
  // AuthenticationUser auth = ConverterUtils.toBeanInstance( params, AuthenticationUser.class );
  // ExampleMatcher matcher = ExampleMatcher.matching().withMatcher( "id",
  // GenericPropertyMatchers.exact() )
  // .withMatcher( "username", GenericPropertyMatchers.startsWith() )
  // .withMatcher( "token", GenericPropertyMatchers.contains() );
  // Example<AuthenticationUser> example = Example.of( auth, matcher );
  // Page<AuthenticationUser> page = repository.findAll( example, pageable );
  // return page.getContent();
  // }
  @RequestMapping(value = "/identity/auth/query", method = RequestMethod.GET,
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.OK)
  public @JsonSerialize List<AuthenticationUser> query(@RequestParam Map<String, Object> params,
      @PageableDefault(sort = {"expires"}) Pageable pageable, HttpSession session)
      throws Exception {
    final String sessionId = session.getId();
    log.debug("sessionId: {}", sessionId);
    AuthenticationUser auth = ConverterUtils.toBeanInstance(params, AuthenticationUser.class);
    ExampleMatcher matcher =
        ExampleMatcher.matching().withMatcher("id", GenericPropertyMatchers.exact())
            .withMatcher("username", GenericPropertyMatchers.startsWith())
            .withMatcher("token", GenericPropertyMatchers.contains());
    Example<AuthenticationUser> example = Example.of(auth, matcher);
    Page<AuthenticationUser> page = repository.findAll(example, pageable);
    return page.getContent();
  }

  @RequestMapping(value = "/identity/auth/details", method = RequestMethod.GET,
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.OK)
  public @JsonSerialize List<AuthenticationUser> users(@RequestParam Map<String, Object> params,
      HttpSession session) {
    final String sessionId = session.getId();
    log.debug("sessionId: {}", sessionId);
    try {
      AuthenticationUser auth = ConverterUtils.toBeanInstance(params, AuthenticationUser.class);
      ExampleMatcher matcher =
          ExampleMatcher.matching().withMatcher("id", GenericPropertyMatchers.exact())
              .withMatcher("username", GenericPropertyMatchers.startsWith())
              .withMatcher("token", GenericPropertyMatchers.contains());
      Example<AuthenticationUser> example = Example.of(auth, matcher);
      return repository.findAll(example).stream().limit(5)
          .sorted(Comparator.comparing(AuthenticationUser::getUsername)).collect(Collectors.toList());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }
}
