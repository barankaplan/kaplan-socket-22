package com.kaplan.client.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.net.Socket;

@Configuration
@PropertySource("file:client/src/main/resources/application-client.properties")

public class SocketConfig {
    @Bean
    @Scope("prototype")
    public Socket createSocket(@Value("${randomServer.host}") String host, @Value("${randomServer.port}") int port)
            throws IOException
    {
        return new Socket(host, port);
    }
}
