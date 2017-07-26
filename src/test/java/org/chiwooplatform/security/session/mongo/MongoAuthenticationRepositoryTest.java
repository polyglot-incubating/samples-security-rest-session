package org.chiwooplatform.security.session.mongo;

import java.util.Collection;
import java.util.stream.Collectors;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.context.support.UUIDGenerator;
import org.chiwooplatform.security.AbstractMongoTests;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.authentication.SimpleToken;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.chiwooplatform.security.core.UserProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = { "home" })
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractMongoTests.class, MongoAuthenticationRepositoryTest.MongoConfiguration.class })
public class MongoAuthenticationRepositoryTest
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Configuration
    static class MongoConfiguration
    {
        @Bean
        public AuthenticationRepository mongoAuthenticationRepository( MongoTemplate mongoTemplate )
        {
            log.info( "mongoTemplate: {}", mongoTemplate );
            return new MongoAuthenticationRepository( mongoTemplate );
        }
        //        @Bean
        //        public String stringSample( MongoTemplate mongoTemplate )
        //        {
        //            log.info( "mongoTemplate: {}", mongoTemplate );
        //            return new String( "stringSample" );
        //        }
    }

    @Autowired
    private MongoAuthenticationRepository authenticationRepository;

    @Test
    public void testObjects()
        throws Exception
    {
        log.info( "mongoTemplate: {}", mongoTemplate );
        log.info( "authenticationRepository: {}", authenticationRepository );
    }

    UserProfile user()
    {
        UserProfile user = new UserProfile( 761120, "lamp@gmail.com", null );
        user.setToken( "zzzyyy-aider-aider-1212112" );
        user.setAuthorities( AuthorityUtils.createAuthorityList( "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER" ) );
        return user;
    }

    @Test
    public void testExists()
        throws Exception
    {
        UserProfile user = user();
        boolean exists = authenticationRepository.exists( user.getUsername() );
        log.info( "exists: {}", exists );
    }

    Collection<String> authorities( UserProfile user )
    {
        Collection<String> authorities = null;
        if ( user.getAuthorities() != null )
        {
            authorities = user.getAuthorities().stream().map( ( v ) -> v.getAuthority() )
                              .collect( Collectors.toList() );
        }
        return authorities;
    }

    @Test
    public void testFindOne()
        throws Exception
    {
        UserProfile user = user();
        AuthenticationUser authUser = authenticationRepository.findOne( user.getUsername() );
        log.info( "Old AuthenticationUser: {}", authUser );
    }
    
    @Test
    public void testGetTokens()
        throws Exception
    {
        UserProfile user = user();
        Collection<SimpleToken> result = authenticationRepository.getTokens(  user.getUsername() );
        log.info( "tokens: {}", result );
    }

    @Test
    public void testAdd()
        throws Exception
    {
        UserProfile user = user();
        RestAuthenticationToken authentication = new RestAuthenticationToken( user );
        Long expires = DateUtils.timeMillis( DateUtils.plusMins( 1 ) );
        authentication.setExpires( expires );
        authenticationRepository.add( authentication );
    }
    
    @Test
    public void testSave()
        throws Exception
    {
        UserProfile user = user();
        AuthenticationUser oldUser = authenticationRepository.findOne( user.getUsername() );
        log.info( "Old AuthenticationUser: {}", oldUser );
        final String token = UUIDGenerator.uuid();
        final Long expires = DateUtils.timeMillis( DateUtils.plusMins( 3 ) );
        oldUser.authentication( token, expires );
        AuthenticationUser newUser = new AuthenticationUser( oldUser );
        log.debug( "New AuthenticationUser: {}", newUser );
         
        authenticationRepository.save( newUser );
    }
}
