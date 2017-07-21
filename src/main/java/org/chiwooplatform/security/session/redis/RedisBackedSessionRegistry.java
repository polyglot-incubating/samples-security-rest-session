package org.chiwooplatform.security.session.redis;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DependsOn("sessionRedisTemplate")
@ConditionalOnBean(RedisOperationsSessionRepository.class)
@Component
public class RedisBackedSessionRegistry extends SpringSessionBackedSessionRegistry {

  private final Logger logger = LoggerFactory.getLogger(RedisBackedSessionRegistry.class);

  @Resource(name = "sessionRedisTemplate")
  private final RedisTemplate<Object, Object> redisTemplate;

  private SetOperations<Object, Object> opsSet;

  private HashOperations<Object, Object, Object> opsHash;
  // private final RedisSerializer<Object> keySerializer;

  // private final FindByIndexNameSessionRepository<ExpiringSession> sessionRepository;
  @SuppressWarnings({"unchecked", "rawtypes"})
  public RedisBackedSessionRegistry(FindByIndexNameSessionRepository sessionRepository,
      RedisTemplate<Object, Object> sessionRedisTemplate) {
    super((FindByIndexNameSessionRepository) sessionRepository);
    this.redisTemplate = sessionRedisTemplate;
    this.opsSet = sessionRedisTemplate.opsForSet();
    this.opsHash = sessionRedisTemplate.opsForHash();
    // this.keySerializer = (RedisSerializer<Object>) redisTemplate.getKeySerializer();
  }

  private static final String PRINCIPAL_PREFIX = "spring:session:index:";

  private static final String DEFAULT_SPRING_SESSION_REDIS_PREFIX = "spring:session:sessions:";

  private static final String SESSION_ATTR_CONTEXT = "sessionAttr:SPRING_SECURITY_CONTEXT";

  private final String principalKey(final String pattern) {
    return RedisBackedSessionRegistry.PRINCIPAL_PREFIX
        + FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME + ":" + pattern;
  }

  private final String sessionKey(final String sessionId) {
    return RedisBackedSessionRegistry.DEFAULT_SPRING_SESSION_REDIS_PREFIX + sessionId;
  }

  private final String lastElement(Set<Object> values) {
    if (values == null) {
      return null;
    }
    Object value = null;
    for (Object val : values) {
      value = val;
    }
    return value.toString();
  }

  @Override
  public List<Object> getAllPrincipals() {
    final Set<Object> rawPrincipals = redisTemplate.keys(principalKey("*"));
    if (rawPrincipals == null || rawPrincipals.size() < 1) {
      return new ArrayList<Object>();
    }
    List<Object> principals = new LinkedList<>();
    for (final Object key : rawPrincipals) {
      Set<Object> values = opsSet.members(key);
      final String sessionId = lastElement(values);
      if (logger.isDebugEnabled()) {
        logger.debug("key: {}, sessionId: {}", key, sessionId);
      }
      try {
        Object value = opsHash.get(sessionKey(sessionId), SESSION_ATTR_CONTEXT);
        if (value == null) {
          redisTemplate.delete(key);
          logger.info("Deleted key '{}'", key);
        } else if (value instanceof SecurityContext) {
          Authentication authentication = ((SecurityContext) value).getAuthentication();
          if (logger.isDebugEnabled()) {
            logger.debug("authentication: {}", authentication);
          }
          principals.add(authentication);
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
    return principals;
  }

  /*
   * private byte[] rawKey( final Object key ) { Assert.notNull( key, "non null key required" ); if
   * ( keySerializer == null && key instanceof byte[] ) { return (byte[]) key; } return
   * keySerializer.serialize( key ); }
   */
  /**
   * TODO 나중에 구현해 보자구.
   * 
   * @param limit
   * @return
   */
  public List<Object> getAllPrincipals(final long limit) {
    final String pattern = principalKey("*");
    RedisCallback<Set<Object>> callback = new RedisCallback<Set<Object>>() {

      public Set<Object> doInRedis(final RedisConnection connection) {
        final ScanOptions options = ScanOptions.scanOptions().match(pattern).count(limit).build();
        Cursor<byte[]> bytes = connection.scan(options);
        Set<Object> keys = new LinkedHashSet<>();
        while (bytes.hasNext()) {
          try {
            String vv = redisTemplate.getStringSerializer().deserialize(bytes.next());
            logger.debug("vv: {}", vv);
            keys.add(vv);
          } catch (Exception e) {
            logger.error(e.getMessage());
          }
        }
        return keys;
      }
    };
    final Set<Object> rawPrincipals = redisTemplate.execute(callback, true);
    return new ArrayList<>(rawPrincipals);
  }
}
