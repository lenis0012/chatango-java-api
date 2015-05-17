package com.lenis0012.chatango.bot.engine;

import com.google.common.base.Joiner;
import com.lenis0012.chatango.bot.ChatangoAPI;
import lombok.SneakyThrows;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public abstract class Codec {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Lock writeLock = new ReentrantLock();

    private boolean connected;
    private boolean firstCommand;
    private WebSocketAdapter adapter;

    public Codec() {
        executorService.scheduleWithFixedDelay(() -> {
            if(connected) {
                sendCommand("");
            }
        }, 20L, 20L, TimeUnit.SECONDS);
    }

    @SneakyThrows
    protected void connect(String host, int port) throws IOException {
        WebSocketClient client = new WebSocketClient();
        client.start();

        URI destination = new URI("ws://" + host + ":" + port);
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols("chat");
        request.setHeader("Origin", "http://st.chatango.com");

        this.adapter = new WebSocketAdapter() {
            @Override
            public void onWebSocketText(String message) {
                super.onWebSocketText(message);
                message = message.endsWith("\r\n") ? message.substring(0, message.length() - "\r\n".length()) : message;
                onMessageReceive(message);
            }

            @Override
            public void onWebSocketConnect(Session sess) {
                super.onWebSocketConnect(sess);
                onConnect();
            }
        };
        Future<Session> future = client.connect(adapter, destination, request);
        future.get();
        this.connected = true;
        this.firstCommand = true;
    }

    public void sendCommand(String... args) {
        if(!connected) {
            return;
        }

        String command = Joiner.on(':').join(args);
        writeLock.lock();
        command = firstCommand ? command : command + "\r\n";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(command.getBytes());
            if(firstCommand) baos.write(0x00);
            adapter.getRemote().sendBytes(ByteBuffer.wrap(baos.toByteArray()));
        } catch(IOException e) {
            ChatangoAPI.getLogger().log(Level.SEVERE, "Error occurred while sending command!", e);
        } finally {
            this.firstCommand = false;
            writeLock.unlock();
        }
    }

    public void onConnect() {}

    public abstract void onMessageReceive(String message);

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }
}
