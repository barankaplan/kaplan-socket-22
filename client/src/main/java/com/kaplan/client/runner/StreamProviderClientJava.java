package com.kaplan.client.runner;

import org.csystem.util.console.Console;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

@Component
@PropertySource("file:client/src/main/resources/application-client.properties")

public class StreamProviderClientJava {
    @Value("${randomServer.host}")
    private String m_host;

    @Value("${randomServerJava.port}")
    private int m_port;

    private void clientCallback()
    {
        try (var socket = new Socket(m_host, m_port)) {
            var dos = new DataOutputStream(socket.getOutputStream());
            var dis = new DataInputStream(socket.getInputStream());
            var br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            var count = Console.readInt("Count?");

            if (count <= 0)
                return;

            var length = Console.readInt("Length?");

            dos.writeInt(count);
            dos.writeInt(length);
            dos.flush();

            if (dis.readBoolean()){
                var password = br.readLine();
                Console.writeLine("%s", password);
            }
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
