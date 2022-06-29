package com.kaplan.server.configuration.client;

import com.kaplan.server.client.ClientInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration

@PropertySource("file:server/src/main/resources/application-server.properties")

public class ClientInfoConfig {
    @Bean
    public Map<Socket, ClientInfo> getClientMap()
    {
        return new ConcurrentHashMap<>();
    }
}
