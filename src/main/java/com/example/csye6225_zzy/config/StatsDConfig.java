package com.example.csye6225_zzy.config;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatsDConfig {


    @Bean
    public StatsDClient statsDClient(){
        return new NonBlockingStatsDClient("zzy_statsD","localhost",8125);
    }
}
