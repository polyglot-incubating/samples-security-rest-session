package org.chiwooplatform.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import org.chiwooplatform.security.session.redis.RedisBackedSessionRegistry;
import org.chiwooplatform.security.session.redis.RedisPermissionResolver;

@ConditionalOnBean(RedisOperationsSessionRepository.class)
@Configuration
public class RedisSessionConfiguration {

    public static final String SESSION_REGISTRY_NAME = "redisBackedSessionRegistry";

    @Autowired
    @Qualifier("sessionRedisTemplate")
    private RedisTemplate<Object, Object> redisTemplate;

    @SuppressWarnings("rawtypes")
    @Autowired
    private FindByIndexNameSessionRepository sessionRepository;

    @Bean(name = RedisSessionConfiguration.SESSION_REGISTRY_NAME)
    public RedisBackedSessionRegistry redisBackedSessionRegistry() {
        final RedisBackedSessionRegistry redisBackedSessionRegistry = new RedisBackedSessionRegistry(sessionRepository,
                redisTemplate);
        return redisBackedSessionRegistry;
    }

    public static final String TOKEN_VALIDATOR_NAME = "redisAuthenticationTokenValidator";

    @Bean(name = TOKEN_VALIDATOR_NAME)
    public RedisPermissionResolver redisAuthenticationTokenValidator() {
        final RedisPermissionResolver redisAuthenticationTokenValidator = new RedisPermissionResolver(
                redisBackedSessionRegistry());
        return redisAuthenticationTokenValidator;
    }

}
