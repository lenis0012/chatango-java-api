package com.lenis0012.chatango.bot.engine;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.lenis0012.chatango.bot.ChatangoAPI;
import com.lenis0012.chatango.bot.api.*;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class Room extends Codec {
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

    // Misc
    @Getter
    private final EventManager eventManager = new EventManager();
    private String uid = "";

    // Settings
    private final Set<User> userList = Sets.newConcurrentHashSet();
    @Getter @Setter
    private Font defaultFont = Font.DEFAULT.clone();
    @Getter @Setter
    private RGBColor nameColor = new RGBColor("000");
    private Channel channel = Channel.DEFAULT;
    private Badge badge = Badge.NONE;

    protected Room(String name, Engine engine) {
        super();
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
        String host = String.format("s%s.chatango.com", getServerId(name));
        int port = 8080;
        connect(host, port);
    }

    @Override
    public void onMessageReceive(String message) {
        roomListener.execute(message);
    }

    protected void login() {
        sendCommand("bauth", name, uid, engine.getCredentials().getUsername(), engine.getCredentials().getPassword());
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
}
