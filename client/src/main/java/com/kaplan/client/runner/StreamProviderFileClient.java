package com.kaplan.client.runner;

import org.csystem.util.console.Console;
import org.csystem.util.net.TcpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Component
@PropertySource("file:client/src/main/resources/application-client.properties")

public class StreamProviderFileClient {
    @Value("${randomServer.host}")
    private String m_host;

    @Value("${randomServerFile.port}")
    private int m_port;

    private void receiveFileProc(Socket socket)
    {
        var filename = TcpUtil.receiveStringViaLength(socket);

        TcpUtil.receiveFile(socket, filename);
    }

    private void clientCallback()
    {
        try (var socket = new Socket(m_host, m_port)) {
            var count = Console.readInt("Count?");

            if (count <= 0)
                return;

            var length = Console.readInt("Number of Characters?");

            TcpUtil.sendInt(socket, count);
            TcpUtil.sendInt(socket, length);

            if (TcpUtil.receiveInt(socket) == 1)
                receiveFileProc(socket);
            else
                Console.writeLine("Invalid parameters for server!...");
        }
        catch (Throwable ex) {
            Console.Error.writeLine(ex.getMessage());
        }
    }

    private void runClient()
    {
        clientCallback();
    }

    public void run() throws Exception
    {
        this.runClient();
    }
}
