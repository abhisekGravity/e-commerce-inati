package com.example.ecommerce.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.MongoDatabaseFactory;

@Configuration
public class MongoTxConfig {

    @Bean
    MongoTransactionManager transactionManager(
            MongoDatabaseFactory factory
    ) {
        return new MongoTransactionManager(factory);
    }
}
