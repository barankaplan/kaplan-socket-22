package com.kaplan.server.configuration.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration

@PropertySource("file:server/src/main/resources/application-server.properties")
public class ExecutorConfig {
    @Bean("executorService.cached")
    public ExecutorService getCachedThreadPool(@Value("${client.maxThread}") int maxThread)
    {
        return Executors.newFixedThreadPool(100);
    }
    //...
}
