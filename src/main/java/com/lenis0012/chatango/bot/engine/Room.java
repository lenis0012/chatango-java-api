package com.lenis0012.chatango.bot.engine;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.lenis0012.chatango.bot.ChatangoAPI;
import com.lenis0012.chatango.bot.api.*;
import com.lenis0012.chatango.bot.events.Event;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class Room extends Thread {
    private static final char END_CHAR = (char) 0x00;
    private static final byte[] BUFFER = new byte[1024];
    private static final List<Entry<String, Integer>> weights = Lists.newArrayList();

    static {
        JsonParser parser = new JsonParser();
        JsonArray list = (JsonArray) parser.parse(new BufferedReader(new InputStreamReader(ChatangoAPI.class.getResourceAsStream("/weights.json"))));
        for(int i = 0; i < list.size(); i++) {
            JsonArray entry = list.get(i).getAsJsonArray();
            weights.add(new SimpleEntry<>(entry.get(0).getAsString(), entry.get(1).getAsInt()));
        }
    }

    private static int getServerId(String name) {
        name = name.toLowerCase().replaceAll("[^0-9a-z]", "q");
        float fnv = (float) new BigInteger(name.substring(0, Math.min(5, name.length())), 36).intValue();
        int lnv = 1000;
        if(name.length() > 6) {
            lnv = new BigInteger(name.substring(6, 6 + Math.min(3, name.length() - 5)), 36).intValue();
            lnv = Math.max(lnv, 1000);
        }
        float num =  (fnv % lnv) / lnv;
        int maxnum = weights.stream().mapToInt(Entry::getValue).sum();
        float sumfreq = 0;
        int sn = 0;
        for(Entry<String, Integer> entry : weights) {
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
    private final RoomListener roomListener;

    // Connection fields
    private Socket socket;
    private OutputStream output;
    private InputStream input;

    // Misc
    @Getter
    private final EventManager eventManager = new EventManager();
    private final Lock writeLock = new ReentrantLock();
    private boolean firstCommand = true;
    private String uid = "";
    private byte[] cache = new byte[0];

    // Settings
    private final Set<User> userList = Sets.newConcurrentHashSet();
    @Getter @Setter
    private Font defaultFont = Font.DEFAULT.clone();
    @Getter @Setter
    private RGBColor nameColor = new RGBColor("000");
    private Channel channel = Channel.DEFAULT;
    private Badge badge = Badge.NONE;

    private boolean connected = false;
    private Thread pingThread = null;

    protected Room(String name, Engine engine) {
        this.name = name;
        this.engine = engine;
        this.roomListener = new RoomListener(this);

        // Generate a UID with 16 characters
        Random random = new Random();
        while(uid.length() < 16) {
            uid += random.nextInt(10);
        }
    }

    public String getRoomName() {
        return name;
    }

    public void connect() throws IOException {
        this.socket = new Socket("s" + getServerId(name) + ".chatango.com", 443);
        socket.setSoTimeout(40000);
        this.output = socket.getOutputStream();
        this.input = socket.getInputStream();
        this.connected = true;
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
                ChatangoAPI.getLogger().log(Level.WARNING, "Failed to read bytes, closing socket!");
                try {
                    socket.close();
                } catch(IOException e1) {}
                connected = false;
            }
        }
    }

    protected void login() {
        sendCommand("bauth", name, uid, engine.getCredentials().getUsername(), engine.getCredentials().getPassword());
        // Constantly ping.
        this.pingThread = new Thread("Room Ping Thread") {
            @Override
            public void run() {
                while(connected) {
                    try {
                        Thread.sleep(20000L);
                    } catch(InterruptedException e) {
                        // Ignore
                    }
                    sendCommand("");
                }
            }
        };
        pingThread.start();
    }

    public void message(Message message) {
        String text = message.getText();
        Font font = message.getFont() == null ? defaultFont : message.getFont();
        String rawFont = Font.encodeFont(font);
        String rawColor = nameColor.encode();
        text = text.replace("\n", "</f></p><p>" + rawFont);
        sendCommand("bm", "t12j", String.valueOf(12 + channel.getId() + badge.getId()), rawColor + rawFont + text.replace("~", "&#126;"));
    }

    protected void addUser(User user) {
        userList.add(user);
    }

    protected void removeUser(User user) {
        userList.remove(user);
    }

    public User findUser(String name) {
        return userList.stream().filter(u -> u.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<User> getUserList() {
        return new ArrayList<>(userList);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
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
        } finally {
            writeLock.unlock();
        }
    }
}
