package org.chiwooplatform.security.session.mongo;

import java.util.Collection;
import java.util.stream.Collectors;

import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.authentication.SimpleToken;
import org.chiwooplatform.security.core.AuthenticationRepository;
import org.chiwooplatform.security.core.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;

/**
 * https://github.com/spring-projects/spring-data-mongodb
 * https://github.com/spring-projects/spring-data-mongodb/blob/9d4d47f503791cc5c7a30cffaaea31f5f8caf870/spring-data-mongodb/src/test/java/org/springframework/data/mongodb/core/ReactiveMongoTemplateTests.java 
 */
public class MongoAuthenticationRepository
    implements AuthenticationRepository
{
    private final transient Logger logger = LoggerFactory.getLogger( MongoAuthenticationRepository.class );

    private final MongoTemplate mongoTemplate;

    private final String collectionName = "Authentications";

    @Autowired
    public MongoAuthenticationRepository( MongoTemplate mongoTemplate )
    {
        super();
        this.mongoTemplate = mongoTemplate;
        logger.debug( "mongoTemplate: {}", mongoTemplate );
    }

    @Override
    public void add( Authentication authentication )
    {
        logger.debug( "authentication: {}", authentication );
        if ( authentication == null || !( authentication instanceof RestAuthenticationToken ) )
        {
            logger.info( "authentication type is '{}'.", authentication.getClass().getName() );
            return;
        }
        RestAuthenticationToken authenticationToken = (RestAuthenticationToken) authentication;
        final String principal = authenticationToken.getPrincipal().toString();
        final String token = authenticationToken.getToken();
        final Long expires = authenticationToken.getExpires();
        try
        {
            UserProfile user = (UserProfile) authenticationToken.getDetails();
            if ( user == null )
            {
                logger.warn( "UserDetails is null." );
                return;
            }
            logger.debug( "UserDetails: {}", user );
            Collection<String> authorities = null;
            if ( user.getAuthorities() != null )
            {
                authorities = user.getAuthorities().stream().map( ( v ) -> v.getAuthority() )
                                  .collect( Collectors.toList() );
            }
            Query findQuery = new Query( Criteria.where( "username" ).is( principal ) );
            boolean exists = mongoTemplate.exists( findQuery, AuthenticationUser.class, collectionName );
            logger.debug( "exists: {}", exists );
            if ( !exists )
            {
                AuthenticationUser authUser = new AuthenticationUser();
                authUser.setAuthorities( authorities );
                authUser.setUsername( principal );
                authUser.setUserId( user.getId() );
                authUser.authentication( token, expires );
                logger.debug( "AuthenticationUser: {}", authUser );
                mongoTemplate.insert( authUser, collectionName );
            }
            else
            {
                throw new DuplicateKeyException( "" );
            }
        }
        catch ( Exception e )
        {
            logger.error( e.getMessage(), e );
        }
    }

    public Collection<SimpleToken> getTokens( String principal )
    {
        Query query = new Query( Criteria.where( "username" ).is( principal ).and( "tokens" ) );
        return mongoTemplate.find( query, SimpleToken.class );
    }

    @Override
    public void save( final AuthenticationUser authUser )
    {
        logger.debug( "authUser: {}", authUser );
        try
        {
            mongoTemplate.save( authUser, collectionName );
        }
        catch ( Exception e )
        {
            logger.error( e.getMessage(), e );
        }
    }

    @Override
    public boolean exists( String principal )
    {
        Query query = new Query( Criteria.where( "username" ).is( principal ) );
        return mongoTemplate.exists( query, collectionName );
    }

    @Override
    public AuthenticationUser findOne( String principal )
    {
        Query query = new Query( Criteria.where( "username" ).is( principal ) );
        return mongoTemplate.findOne( query, AuthenticationUser.class, collectionName );
    }

    @Override
    public Collection<AuthenticationUser> findAll( Query query )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
