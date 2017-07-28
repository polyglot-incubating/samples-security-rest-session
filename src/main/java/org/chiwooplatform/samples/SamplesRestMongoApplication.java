package org.chiwooplatform.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

@EnableMongoRepositories("org.chiwooplatform.samples.dao.mongo")
@SpringBootApplication
public class SamplesRestMongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SamplesRestMongoApplication.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.indentOutput(true);
        builder.failOnUnknownProperties(false);
        return builder;
    }
}
