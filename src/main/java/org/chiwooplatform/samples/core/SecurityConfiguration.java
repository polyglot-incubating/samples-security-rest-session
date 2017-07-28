package org.chiwooplatform.samples.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
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

import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.samples.support.DefaultCorsConfiguration;
import org.chiwooplatform.security.authentication.AuthenticationUser;
import org.chiwooplatform.security.configuration.EnableRedisSessionRegistry;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.chiwooplatform.security.core.UserAuthoritzLoader;
import org.chiwooplatform.security.core.UserProfileResolver;
import org.chiwooplatform.security.session.mongo.MongoAuthenticationRepository;
import org.chiwooplatform.security.session.redis.RedisBackedSessionRegistry;
import org.chiwooplatform.security.support.AnonymousStatelessAuthenticationFilter;
import org.chiwooplatform.security.support.JdbcUserAuthoritzLoader;
import org.chiwooplatform.security.support.JdbcUserProfileResolver;
import org.chiwooplatform.security.support.RestAuthenticationFilter;
import org.chiwooplatform.security.support.RestAuthenticationProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by aider on 2017-07-18.
 */
@EnableRedisSessionRegistry
@EnableWebSecurity
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
            .antMatchers("/api/**").authenticated()
            .antMatchers(HttpMethod.POST, "/identity/**").permitAll()
            .antMatchers("/admin/**").hasRole("ADMIN")
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

    @Bean
    public RestAuthenticationFilter restAuthenticationFilter(ObjectMapper objectMapper)
            throws Exception {
        final String loginUri = "/identity/auth/tokens";
        final RestAuthenticationFilter filter = new RestAuthenticationFilter(loginUri,
                objectMapper);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Autowired
    private RestAuthenticationFilter restAuthenticationFilter;

    private UserAuthoritzLoader userAuthoritzLoader(JdbcTemplate jdbcTemplate) {
        final JdbcUserAuthoritzLoader jdbcUserAuthoritzLoader = new JdbcUserAuthoritzLoader(
                jdbcTemplate);
        return jdbcUserAuthoritzLoader;
    }

    private MongoAuthenticationRepository authenticationRepository(
            MongoTemplate mongoTemplate) {
        final MongoAuthenticationRepository repository = new MongoAuthenticationRepository(
                mongoTemplate);
        return repository;
    }

    private UserProfileResolver userProfileResolver(JdbcTemplate jdbcTemplate) {
        final JdbcUserProfileResolver userPrincipalResolver = new JdbcUserProfileResolver(
                jdbcTemplate);
        userPrincipalResolver.setUserAuthoritzLoader(userAuthoritzLoader(jdbcTemplate));
        return userPrincipalResolver;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(JdbcTemplate jdbcTemplate,
            MongoTemplate mongoTemplate) {
        final UserProfileResolver userProfileResolver = userProfileResolver(jdbcTemplate);
        final AuthenticationRepository<AuthenticationUser> authenticationRepository = authenticationRepository(
                mongoTemplate);
        // System.out.println("UserProfileResolver ----- " + userProfileResolver);
        // System.out.println("AuthenticationRepository ----- " +
        // authenticationRepository);
        final RestAuthenticationProvider provider = new RestAuthenticationProvider(
                userProfileResolver);
        provider.setAuthenticationRepository(authenticationRepository);
        return provider;
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
