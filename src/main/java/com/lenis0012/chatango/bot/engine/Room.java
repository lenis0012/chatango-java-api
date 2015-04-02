package com.lenis0012.chatango.bot.engine;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.lenis0012.chatango.bot.Main;
import com.lenis0012.chatango.bot.api.Message;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class Room extends Thread {
    private static final char END_CHAR = (char) 0x00;
    private static final byte[] BUFFER = new byte[1024];
    private static final Map<String, Integer> weights = Maps.newConcurrentMap();

    static {
        try {
            JsonParser parser = new JsonParser();
            JsonArray list = (JsonArray) parser.parse(new FileReader(new File("weights.json")));
            for(int i = 0; i < list.size(); i++) {
                JsonArray entry = list.get(i).getAsJsonArray();
                String key = entry.get(0).getAsString();
                int val = entry.get(1).getAsInt();
                weights.put(key, val);
            }
        } catch(FileNotFoundException e) {
            System.err.println("Missing weights.json file!");
        }
    }

    private static int getServerId(String name) {
        float fnv = (float) new BigInteger(name.substring(0, Math.min(5, name.length())), 36).intValue();
        int lnv = 1000;
        if(name.length() > 6) {
            lnv = new BigInteger(name.substring(6, 6 + Math.min(3, name.length() - 5)), 36).intValue();
            lnv = Math.max(lnv, 1000);
        }
        float num =  (fnv % lnv) / lnv;
        int maxnum =  weights.values().stream().mapToInt(Integer::intValue).sum();
        float sumfreq = 0;
        int sn = 0;
        for(Entry<String, Integer> entry : weights.entrySet()) {
            sumfreq += ((float) entry.getValue()) / maxnum;
            if(num <= sumfreq) {
                sn = Integer.parseInt(entry.getKey());
                break;
            }
        }
        return sn;
    }

    private final String name;
    private final Engine engine;

    // Connection fields
    private final Socket socket;
    private final OutputStream output;
    private final InputStream input;
    private final RoomListener roomListener;

    // Misc
    private final Lock writeLock = new ReentrantLock();
    private boolean firstCommand = true;
    private String uid = "";
    private byte[] cache = new byte[0];

    protected Room(String name, Engine engine) throws IOException {
        this.name = name;
        this.engine = engine;
        this.socket = new Socket("s" + getServerId(name) + ".chatango.com", 443);
        this.output = socket.getOutputStream();
        this.input = socket.getInputStream();
        this.roomListener = new RoomListener(this);

        Random random = new Random();
        while(uid.length() < 16) {
            uid += random.nextInt(10);
        }
        System.out.println(uid);
    }

    @Override
    public void run() {
        while(socket.isConnected()) {
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
                            roomListener.execute(new String(bytes));
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
                Main.getLogger().log(Level.WARNING, "Failed to read bytes!");
            }
        }
    }

    protected void login() {
        sendCommand("bauth", name, uid, engine.getCredentials().getUsername(), engine.getCredentials().getPassword());
        this.writeLock.lock();
    }

    protected void onMessage(Message message) {
        Main.getLogger().log(Level.INFO, String.format("%s: %s", message.getUser().getName(), message.getText()));
    }

    protected void sendCommand(String... args) {
        String command = Joiner.on(':').join(args);
        command = firstCommand ? command: command + "\r\n";
        this.firstCommand = false;
        writeLock.lock();
        try {
            output.write(command.getBytes());
            output.write(0x00);
            output.flush();
        } catch(IOException e) {
            Main.getLogger().log(Level.SEVERE, "Error occurred while sending command", e);
        } finally {
            writeLock.unlock();
        }
    }
}
