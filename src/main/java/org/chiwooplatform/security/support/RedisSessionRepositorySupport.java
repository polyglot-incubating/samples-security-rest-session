package org.chiwooplatform.security.support;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.FindByIndexNameSessionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisSessionRepositorySupport {

  private final Logger logger = LoggerFactory.getLogger(RedisSessionRepositorySupport.class);

  @Autowired
  private RedisTemplate<Object, Object> redisTemplate;

  BoundHashOperations<Object, Object, Object> boundHashOps(final String key) {
    return redisTemplate.boundHashOps(key);
  }

  BoundValueOperations<Object, Object> boundValueOps(final String key) {
    return redisTemplate.boundValueOps(key);
  }

  Collection<Object> hvals(final String key) {
    return redisTemplate.<String, Object>opsForHash().values(key);
  }

  public RedisSessionRepositorySupport() {
    super();
  }

  public Map<Object, Object> hgetall(final String sessionKey) {
    return boundHashOps(sessionKey).entries();
  }

  private String principalKey() {
    return FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME;
  }

  public Object getAllPrincipals() {
    logger.info("getAllPrincipals()");
    Object obj = redisTemplate.keys(principalKey() + "*");
    logger.info("obj.getClass: {}", obj.getClass().getName());
    // LinkedHashSet<?> keys = (LinkedHashSet)redisTemplate.keys( principalKey() + "*" );
    // keys.forEach( (v) -> {
    // logger.debug( "v: {}", v.getClass().getName() );
    // logger.debug( "key: {}", v );
    // } );
    return obj;
  }
  // public Stream<String> getAllSessionIds()
  // {
  // final Set<Object> keys = redisTemplate.keys( REDIS_SESSION_PREFIX + "*" );
  // //log.trace( "Got all session ids {}", keys );
  // return keys.stream().map( key -> key.toString().replace( REDIS_SESSION_PREFIX, "" ) );
  // }
}
