package org.chiwooplatform.samples.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;

import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.samples.support.DefaultCorsConfiguration;
import org.chiwooplatform.security.configuration.EnableRedisSessionRegistry;
import org.chiwooplatform.security.core.UserPrincipalResolver;
import org.chiwooplatform.security.session.redis.RedisBackedSessionRegistry;
import org.chiwooplatform.security.support.AnonymousStatelessAuthenticationFilter;
import org.chiwooplatform.security.support.JdbcUserPrincipalResolver;
import org.chiwooplatform.security.support.RestAuthenticationProvider;

/**
 * Created by aider on 2017-07-18.
 */
@EnableRedisSessionRegistry
@EnableWebSecurity
public class SecurityConfiguration
    extends WebSecurityConfigurerAdapter {

    private static final String[] EXCLUDED_WEB_STATIC_RESOURCES = new String[] {
        "/static/**",
        "/assets/**",
        "/resources/**",
        "/favicon.ico",
        "/css/**",
        "/js/**" };

    public SecurityConfiguration() {
        super( true );
    }

    protected String[] staticUriPatterns() {
        return null;
    }

    private String[] ignoringPaths() {
        String[] uris = staticUriPatterns();
        if ( uris == null ) {
            return EXCLUDED_WEB_STATIC_RESOURCES;
        }
        return uris;
    }

    @Override
    public void configure( WebSecurity web )
        throws Exception {
        // @formatter:off
        web.ignoring()
            .antMatchers( HttpMethod.OPTIONS, "/**" )
            .antMatchers( "/h2-console/**" )
            .antMatchers( ignoringPaths() );
        // @formatter:on
        // Apply-EL TO JSP-VIEW
        // DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        // handler.setPermissionEvaluator( permissionEvaluator() );
        // web.expressionHandler( handler );
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisBackedSessionRegistry sessionRegistry;

    @Override
    protected void configure( HttpSecurity http )
        throws Exception {
        DefaultCorsConfiguration cors = new DefaultCorsConfiguration();
        //@formatter:off		
		http.csrf().disable()
		    .cors().configurationSource( cors )
		    .and().anonymous().authenticationFilter( new AnonymousStatelessAuthenticationFilter( UUIDGenerator.uuid() ) )
        	.and()
                .httpBasic().disable()                
                .headers()
                    .addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "script-src 'self'"))
                        .frameOptions().disable()
            .and()
            .authorizeRequests()            
    			.antMatchers("/api/**").authenticated()
    			.antMatchers( HttpMethod.POST, "/identity/**" ).permitAll()
    			.antMatchers("/admin/**").hasRole("ADMIN")
    			.anyRequest().permitAll()
			.and().requestCache().requestCache(new NullRequestCache())
			.and()
			    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
			    .maximumSessions( 2 )
			    .sessionRegistry( sessionRegistry )
			.and()
			.and().securityContext()
			.and().servletApi()
			.and().authenticationProvider( authenticationProvider() )
			.addFilter( new WebAsyncManagerIntegrationFilter() )
			.logout();
		// @formatter:on
    }

    @Bean
    public UserPrincipalResolver userPrincipalResolver() {
        final JdbcUserPrincipalResolver userPrincipalResolver = new JdbcUserPrincipalResolver( jdbcTemplate );
        return userPrincipalResolver;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final RestAuthenticationProvider authenticationProvider = new RestAuthenticationProvider( userPrincipalResolver() );
        return authenticationProvider;
    }

    @Bean
    public HeaderHttpSessionStrategy headerHttpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }
    //    // @ConditionalOnBean(name = "sessionRedisTemplate")
    //    @ConditionalOnBean(RedisOperationsSessionRepository.class)
    //    @Bean
    //    public RedisBackedSessionRegistry sessionRegistry() {
    //        System.out.println( "@Bean RedisBackedSessionRegistry" );
    //        return new RedisBackedSessionRegistry( sessionRepository );
    //    }
}
