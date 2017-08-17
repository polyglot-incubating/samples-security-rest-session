package org.chiwooplatform.security;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import org.chiwooplatform.security.configuration.EnableRedisSessionRegistry;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@ImportAutoConfiguration(classes = { RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class })
@EnableRedisSessionRegistry
public class AbstractRedisTests {

}
