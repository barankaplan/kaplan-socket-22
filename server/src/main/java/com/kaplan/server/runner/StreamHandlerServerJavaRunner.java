package com.kaplan.server.runner;

import mainpackage.Sender;
import org.csystem.util.console.Console;
import org.csystem.util.scheduler.Scheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import com.kaplan.server.client.ClientInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.csystem.util.exception.ExceptionUtil.subscribeRunnable;

@Component

@PropertySource("file:server/src/main/resources/application-server.properties")

public class StreamHandlerServerJavaRunner implements ApplicationRunner {
    private final ServerSocket m_serverSocket;
    private final ExecutorService m_threadPool;
    private final Map<Socket, ClientInfo> m_clients;

    @Value("${stream.secondpass}")
    private int m_passwordMaxLength;

    @Value("${stream.firstpass}")
    private int m_passwordMaxCount;

    @Value("${scheduler.interval}")
    private int m_schedulerInterval;

    @Value("${scheduler.intervalUnitStr}")
    private String m_schedulerIntervalUnitStr;

    @Value("${scheduler.threshold}")
    private int m_schedulerThreshold;

    @Value("${scheduler.thresholdUnitStr}")
    private String m_schedulerThresholdUnitStr;

    private void handleClient(Socket clientSocket) {
        m_threadPool.execute(() -> generateStream(clientSocket));
    }

    private void handleClient2(Socket clientSocket) {
        m_threadPool.execute(() -> generateStream(clientSocket));
    }

    private void send(int count, int length, BufferedWriter bw, DataOutputStream dos) throws IOException {
        boolean status = count == m_passwordMaxCount && length == m_passwordMaxLength;

        dos.writeBoolean(status);

        if (status)
            sendStream(count, length, bw);
    }

    private void sendStream(int count, int length, BufferedWriter bw) throws IOException {


//            var text = StringUtil.getRandomTextEN(random, length);
            var text = Sender.distinctProducts("products.csv",count+length);


            //Console.writeLine("%s ", text);
            bw.write(text + "\r\n");
            bw.flush();

    }

    private void clientsSynchronize(Runnable runnable) {
        synchronized (m_clients) {
            runnable.run();
        }
    }

    private void setLastTimeCallback(ClientInfo clientInfo) {
        if (!m_clients.containsKey(clientInfo.getSocket()))
            return;

        clientInfo.setLastTime(LocalDateTime.now());
    }

    private void setCompletedCallback(ClientInfo clientInfo) {
        synchronized (m_clients) {
            if (!m_clients.containsKey(clientInfo.getSocket()))
                return;

            clientInfo.setCompleted();
        }
    }

    private void generateStreamCallback(Socket clientSocket) throws IOException {
        var clientInfo = new ClientInfo(clientSocket, clientSocket.getPort());
        m_clients.put(clientSocket, clientInfo);
        var dis = new DataInputStream(clientSocket.getInputStream());
        var dos = new DataOutputStream(clientSocket.getOutputStream());
        var bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        int count = dis.readInt();

        clientsSynchronize(() -> setLastTimeCallback(clientInfo));

        int length = dis.readInt();

        clientsSynchronize(() -> setCompletedCallback(clientInfo));

        send(count, length, bw, dos);
    }

    private void generateStream(Socket clientSocket) {
        Console.writeLine("Host: %s, Port: %d, Local Port: %d%n", clientSocket.getInetAddress().getHostAddress(),
                clientSocket.getPort(), clientSocket.getLocalPort());

        subscribeRunnable(() -> generateStreamCallback(clientSocket), clientSocket,
                ex -> Console.Error.writeLine("generatePasswords:%s", ex.getMessage()), () -> m_clients.remove(clientSocket));
    }

    private void acceptClient() throws IOException {
        Console.writeLine("Kaplan Server is waiting for a client");

        handleClient(m_serverSocket.accept());
    }

    private void runForAccept() {
        for (; ; )
            subscribeRunnable(this::acceptClient, ex -> Console.Error.writeLine(ex.getMessage()));
    }

    private void runServerCallback() {
        runForAccept();
    }

    private boolean isRemovable(Socket socket, int threshold, String schedulerThresholdUnitStr) {
        var now = LocalDateTime.now();
        var ci = m_clients.get(socket);

        var status = ChronoUnit.valueOf(schedulerThresholdUnitStr).between(ci.getLastTime(), now) > threshold;

        subscribeRunnable(() -> {
            if (status) socket.close();
        }, ex -> Console.Error.writeLine(ex.getMessage()));

        return status;
    }

    private void schedulerSynchronizedCallback() {
        m_clients.keySet().removeIf(s -> isRemovable(s, m_schedulerThreshold, m_schedulerThresholdUnitStr));
    }

    private void schedulerCallback() {
        //Console.writeLine("Client Size:%d", m_clients.size());
        clientsSynchronize(this::schedulerSynchronizedCallback);
    }

    public StreamHandlerServerJavaRunner(ServerSocket serverSocket, ExecutorService threadPool, Map<Socket, ClientInfo> clients) {
        m_serverSocket = serverSocket;
        m_threadPool = threadPool;
        m_clients = clients;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Scheduler(m_schedulerInterval, TimeUnit.valueOf(m_schedulerIntervalUnitStr)).schedule(this::schedulerCallback);
        m_threadPool.execute(this::runServerCallback);
    }
}
