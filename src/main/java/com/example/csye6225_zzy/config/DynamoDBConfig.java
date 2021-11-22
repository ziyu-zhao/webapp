package com.example.csye6225_zzy.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfig {

    @Bean
    public DynamoDB dynamoDB(){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        return new DynamoDB(client);
    }
}
