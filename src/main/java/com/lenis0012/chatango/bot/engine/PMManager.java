package com.lenis0012.chatango.bot.engine;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lenis0012.chatango.bot.ChatangoAPI;
import com.lenis0012.chatango.bot.api.Friend;
import com.lenis0012.chatango.bot.events.pm.PMMessageEvent;
import com.lenis0012.chatango.bot.events.pm.PMTrackEvent;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

public class PMManager extends Codec {
    private final Map<String, Method> methods = Maps.newHashMap();
    private final Map<String, Set<Consumer<String[]>>> events = Maps.newConcurrentMap();
    private final List<Friend> friendList = Collections.synchronizedList(Lists.newArrayList());
    private final Engine engine;
    private Consumer<PMMessageEvent> onMessage;
    private Runnable onConnect;

    public PMManager(Engine engine) {
        super();
        this.engine = engine;
        for(Method method : getClass().getDeclaredMethods()) {
            if(method.getName().startsWith("on")) {
                methods.put(method.getName().substring(2).toLowerCase(), method);
                method.setAccessible(true);
            }
        }
    }

    public void connect() {
        try {
            connect("c1.chatango.com", 5222);
            sendCommand("tlogin", engine.getCredentials().getAuthKey(), "2");
        } catch(IOException e) {
            ChatangoAPI.getLogger().log(Level.WARNING, "Failed to connect to PM server", e);
        }
    }

    @Override
    @SneakyThrows
    public void onMessageReceive(String message) {
        message = message.replace("\r\n", ""); // Removing end line
        if(message.isEmpty()) return;
        System.out.println(message);

        String[] data = message.split(":");
        String cmd = data[0].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String[] args = new String[data.length - 1];
        System.arraycopy(data, 1, args, 0, args.length);

        Method method = methods.get(cmd);
        if(method != null) {
            method.invoke(this, (Object) args);
        } else {
            Set<Consumer<String[]>> consumers = events.get(cmd);
            if(consumers != null) {
                consumers.forEach(c -> c.accept(args));
                consumers.clear();
            }
        }
    }

    public void message(String user, String message) {
        sendCommand("msg", user, message);
    }

    public Friend getFriend(String name) {
        return friendList.stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Friend> getFriends() {
        return new ArrayList<>(friendList);
    }

    public void onMessage(Consumer<PMMessageEvent> consumer) {
        this.onMessage = consumer;
    }

    public void onConnect(Runnable consumer) {
        this.onConnect = consumer;
    }

    public void track(String user, Consumer<PMTrackEvent> ev) {
        sendCommand("track", user);
        addConsumer("track", args -> {
            int idle = Integer.parseInt(args[1]);
            boolean online = "online".equalsIgnoreCase(args[2]);
            ev.accept(new PMTrackEvent(args[0], online, idle));
        });
    }

    private void onOK(String[] args) {
        sendCommand("wl"); // Request friend list
    }

    private void onWL(String[] args) {
        //idleupdate:name:newidle
        //wloffline:name:time
        //wlonline:name:time
        for(int i = 0; i < args.length / 4; i++) {
            String name = args[i * 4 + 0];
            long lastOnline = Long.parseLong(args[i * 4 + 1]);
            boolean online = "on".equals(args[i * 4 + 2]);
            int idle = Integer.parseInt(args[i * 4 + 3]);
            friendList.add(new Friend(name, online, idle, lastOnline));
        }
        if(onConnect != null) {
            onConnect.run();
        }
    }

    private void onMsg(String[] args) {
        parseMessage(args);
    }

    private void onMsgOff(String[] args) {
        parseMessage(args);
    }

    protected void parseMessage(String[] args) {
        String user = args[0];
        StringBuilder builder = new StringBuilder();
        for(int i = 5; i < args.length; i++) {
            builder.append(args[i]).append(":");
        }
        builder.setLength(Math.max(0, builder.length() - 1));
        String message = builder.toString().replaceAll("<.*?>", "");
        Friend friend = getFriend(user);
        if(friend != null) {
            friend.addMessage("O" + message); // Not self
        }
        if(onMessage != null) {
            onMessage.accept(new PMMessageEvent(user, message));
        }
    }

    private void addConsumer(String cmd, Consumer<String[]> ev) {
        Set<Consumer<String[]>> set = events.get(cmd);
        if(set == null) {
            set = Sets.newConcurrentHashSet();
            events.put(cmd, set);
        }

        set.add(ev);
    }
}
