package com.kaplan.client.helper;


import com.kaplan.client.runner.StreamProviderClient;
import com.kaplan.client.runner.StreamProviderClientJava;
import com.kaplan.client.runner.StreamProviderStayConnected;
import com.kaplan.client.runner.StreamProviderFileClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:client/src/main/resources/application-client.properties")

public class StreamProviderHelper {
    private final StreamProviderClientJava m_clientJava;
    private final StreamProviderClient m_client;
    private final StreamProviderStayConnected m_clientStayConnected;
    private final StreamProviderFileClient m_clientFile;

    public StreamProviderHelper(StreamProviderClientJava clientJava, StreamProviderClient client,
                                StreamProviderStayConnected clientStayConnected, StreamProviderFileClient clientFile)
    {
        m_clientJava = clientJava;
        m_client = client;
        m_clientStayConnected = clientStayConnected;
        m_clientFile = clientFile;
    }

    public void runClientJava() throws Exception
    {
        m_clientJava.run();
    }

    public void runClient() throws Exception
    {
        m_client.run();
    }

    public void runClientStayConnected() throws Exception
    {
        m_clientStayConnected.run();
    }

    public void runClientFile() throws Exception
    {
        m_clientFile.run();
    }
}
