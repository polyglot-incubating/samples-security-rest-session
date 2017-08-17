package org.chiwooplatform.security;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@ImportAutoConfiguration(classes = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class })
public class AbstractMongoTests {
}
