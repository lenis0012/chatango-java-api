package com.lenis0012.chatango.bot.engine;

import com.google.common.base.Joiner;
import com.lenis0012.chatango.bot.ChatangoAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public abstract class SCodec extends Thread implements Codec {
    private static final char END_CHAR = (char) 0x00;
    private static final byte[] BUFFER = new byte[1024];

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Lock writeLock = new ReentrantLock();

    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private boolean connected;
    private boolean firstCommand;
    private byte[] cache = new byte[0];

    public SCodec() {
        executorService.scheduleWithFixedDelay(() -> {
            if(connected) {
                sendCommand("");
            }
        }, 20L, 20L, TimeUnit.SECONDS);
    }

    public void connect(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        socket.setSoTimeout(40000); // 40 seconds
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
        this.connected = true;
        this.firstCommand = true;
        onConnect();
        start();
    }

    @Override
    public void run() {
        while(connected) {
            try {
                int len = input.read(BUFFER);
                if(len > 0) {
                    int start = 0;
                    // Scan through bytes to find new commands
                    for(int i = 0; i < len; i++) {
                        if(BUFFER[i] == END_CHAR) {
                            // Add previous cache if remaining
                            byte[] bytes = new byte[cache.length + (i - start)];
                            System.arraycopy(cache, 0, bytes, 0, cache.length);
                            System.arraycopy(BUFFER, start, bytes, cache.length, i - start);
                            onMessageReceive(new String(bytes));
                            this.cache = new byte[0];
                            start = i + 1;
                        }
                    }

                    // Add remainder to cache
                    byte[] newCache = new byte[cache.length + (len - start)];
                    System.arraycopy(cache, 0, newCache, 0, cache.length);
                    System.arraycopy(BUFFER, start, newCache, cache.length, len - start);
                    this.cache = newCache;
                }
            } catch(IOException e) {
                ChatangoAPI.getLogger().log(Level.WARNING, "Failed to read bytes, closing socket!");
                try {
                    socket.close();
                } catch(IOException e1) {}
                this.connected = false;
            }
        }
    }

    public void sendCommand(String... args) {
        if(!connected) {
            return;
        }

        String command = Joiner.on(':').join(args);
        command = firstCommand ? command : command + "\r\n";
        this.firstCommand = false;
        writeLock.lock();
        try {
            output.write(command.getBytes());
            output.write(0x00);
            output.flush();
        } catch(IOException e) {
            ChatangoAPI.getLogger().log(Level.SEVERE, "Error occurred while sending command, closing socket!", e);
            try {
                socket.close();
            } catch(IOException e1) {}
            this.connected = false;
        } finally {
            writeLock.unlock();
        }
    }

    public void onConnect() {
    }

    public abstract void onMessageReceive(String message);

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }
}
