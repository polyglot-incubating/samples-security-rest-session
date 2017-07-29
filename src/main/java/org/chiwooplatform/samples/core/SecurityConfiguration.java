package org.chiwooplatform.samples.core;

import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.samples.support.DefaultCorsConfiguration;
import org.chiwooplatform.security.configuration.EnableRedisSessionRegistry;
import org.chiwooplatform.security.core.PermissionResolver;
import org.chiwooplatform.security.core.UserProfileResolver;
import org.chiwooplatform.security.session.mongo.MongoAuthenticationRepository;
import org.chiwooplatform.security.session.mongo.MongoPermissionResolver;
import org.chiwooplatform.security.session.redis.RedisBackedSessionRegistry;
import org.chiwooplatform.security.support.AnonymousStatelessAuthenticationFilter;
import org.chiwooplatform.security.support.JdbcUserAuthoritzLoader;
import org.chiwooplatform.security.support.JdbcUserProfileResolver;
import org.chiwooplatform.security.support.RestAuthenticationFilter;
import org.chiwooplatform.security.support.RestAuthenticationProvider;
import org.chiwooplatform.security.support.TokenPermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by aider on 2017-07-18.
 */
@EnableRedisSessionRegistry
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] EXCLUDED_WEB_STATIC_RESOURCES = new String[] {
            "/static/**", "/assets/**", "/resources/**", "/favicon.ico", "/css/**",
            "/js/**" };

    public SecurityConfiguration() {
        // super(true);
    }

    protected String[] staticUriPatterns() {
        return null;
    }

    private String[] ignoringPaths() {
        String[] uris = staticUriPatterns();
        if (uris == null) {
            return EXCLUDED_WEB_STATIC_RESOURCES;
        }
        return uris;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    // @formatter:off
    web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**").antMatchers("/h2-console/**")
        .antMatchers(ignoringPaths());
    // @formatter:on
        // Apply-EL TO JSP-VIEW
        // DefaultWebSecurityExpressionHandler handler = new
        // DefaultWebSecurityExpressionHandler();
        // handler.setPermissionEvaluator( permissionEvaluator() );
        // web.expressionHandler( handler );
    }

    @Autowired
    private RedisBackedSessionRegistry sessionRegistry;

    private static final String API_LOGIN_URI = "/identity/auth/tokens";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        DefaultCorsConfiguration cors = new DefaultCorsConfiguration();
    // @formatter:off
    http.csrf().disable().cors().configurationSource(cors)
        .and().anonymous().authenticationFilter(new AnonymousStatelessAuthenticationFilter(UUIDGenerator.uuid()))
        .and().httpBasic().disable()
        .headers().addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "script-src 'self'"))
            .frameOptions().disable()
        .and().authorizeRequests()
            // URI 에 대한 ROLE Votor 를 커스터마이즈 하고 싶다면
            // .accessDecisionManager(accessDecisionManager())
            .antMatchers("/api/admin/**").hasRole("ADMIN")
            .antMatchers("/api/manager/**" ).hasRole("MANAGER")
            .antMatchers("/api/user/**").hasAnyRole("ADMIN", "MANAGER", "USER")
            .antMatchers("/api/**").authenticated()
            .antMatchers(HttpMethod.POST, API_LOGIN_URI).permitAll()
            .anyRequest().authenticated()
        .and().requestCache().requestCache(new NullRequestCache())
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .maximumSessions(2).maxSessionsPreventsLogin(true)
            .sessionRegistry(sessionRegistry).and()
        .and().securityContext()
        .and().servletApi()
        // .and().formLogin()
        .and().logout()
        .and().authenticationProvider(authenticationProvider)
        .addFilter(new WebAsyncManagerIntegrationFilter())
        .addFilterBefore( restAuthenticationFilter, UsernamePasswordAuthenticationFilter.class );
    // @formatter:on
    }

    // /**
    // * URI 에 대한 ROLE Votor 를 커스터마이즈 하고 싶다면, AccessDecisionVoter 인터페이스를 구현 하여 AccessDecisionManager 에 추가 할 수 있다.
    // * @return
    // */
    // @Bean
    // public AccessDecisionManager accessDecisionManager() {
    // List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
    // new WebExpressionVoter(), new RoleVoter(), new AuthenticatedVoter()/*
    // * , new MinuteBasedVoter()
    // */
    // );
    // return new UnanimousBased(decisionVoters);
    // }

    @Bean
    public RestAuthenticationFilter restAuthenticationFilter(ObjectMapper objectMapper)
            throws Exception {
        final RestAuthenticationFilter filter = new RestAuthenticationFilter(
                API_LOGIN_URI);
        filter.setObjectMapper(objectMapper);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Autowired
    private RestAuthenticationFilter restAuthenticationFilter;

    @Bean
    public MongoAuthenticationRepository mongoAuthenticationRepository(
            MongoTemplate mongoTemplate) {
        final MongoAuthenticationRepository repository = new MongoAuthenticationRepository(
                mongoTemplate);
        return repository;
    }

    @Bean
    public UserProfileResolver userProfileResolver(JdbcTemplate jdbcTemplate) {
        final JdbcUserProfileResolver principalResolver = new JdbcUserProfileResolver(
                jdbcTemplate);
        final JdbcUserAuthoritzLoader authoritzLoader = new JdbcUserAuthoritzLoader(
                jdbcTemplate);
        principalResolver.setUserAuthoritzLoader(authoritzLoader);
        return principalResolver;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserProfileResolver userProfileResolver,
            MongoAuthenticationRepository authenticationRepository) {
        final RestAuthenticationProvider provider = new RestAuthenticationProvider(
                userProfileResolver);
        provider.setAuthenticationRepository(authenticationRepository);
        return provider;
    }

    @Bean
    public PermissionResolver permissionResolver(MongoTemplate mongoTemplate) {
        final MongoPermissionResolver permissionResolver = new MongoPermissionResolver(
                mongoTemplate);
        return permissionResolver;
    }

    @Bean
    public PermissionEvaluator permissionEvaluator(
            PermissionResolver permissionResolver) {
        final TokenPermissionEvaluator permissionEvaluator = new TokenPermissionEvaluator(
                permissionResolver);
        return permissionEvaluator;
    }

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public HeaderHttpSessionStrategy headerHttpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }
    // // @ConditionalOnBean(name = "sessionRedisTemplate")
    // @ConditionalOnBean(RedisOperationsSessionRepository.class)
    // @Bean
    // public RedisBackedSessionRegistry sessionRegistry() {
    // System.out.println( "@Bean RedisBackedSessionRegistry" );
    // return new RedisBackedSessionRegistry( sessionRepository );
    // }
}
