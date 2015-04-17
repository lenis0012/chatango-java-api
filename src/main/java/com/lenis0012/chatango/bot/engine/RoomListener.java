package com.lenis0012.chatango.bot.engine;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.lenis0012.chatango.bot.ChatangoAPI;
import com.lenis0012.chatango.bot.api.Font;
import com.lenis0012.chatango.bot.api.Message;
import com.lenis0012.chatango.bot.api.RGBColor;
import com.lenis0012.chatango.bot.api.User;
import com.lenis0012.chatango.bot.events.ConnectEvent;
import com.lenis0012.chatango.bot.events.MessageReceiveEvent;
import com.lenis0012.chatango.bot.events.UserJoinEvent;
import com.lenis0012.chatango.bot.events.UserLeaveEvent;
import com.lenis0012.chatango.bot.utils.Utils;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomListener {
    private static final Pattern ID_PATTERN = Pattern.compile("<n(.*?)/>");
    private static final Pattern FONT_PATTERN = Pattern.compile("<f(.*?)>");

    private final Map<String, Method> methods = Maps.newConcurrentMap();
    private final Room room;

    public RoomListener(Room room) {
        this.room = room;
        for(Method method : getClass().getMethods()) {
            if(method.getName().startsWith("on")) {
                methods.put(Character.toLowerCase(method.getName().charAt(2)) + method.getName().substring(3), method);
            }
        }
    }

    public void execute(String cmd) {
        String name = cmd.split(":")[0];
        String[] args = cmd.contains(":") ? cmd.substring(name.length() + 1).split(":") : new String[0 ];
        Method method = methods.get(Utils.pythToJav(name));
        if(method != null) {
            try {
                method.invoke(this, (Object) args);
            } catch(Exception e) {
                ChatangoAPI.getLogger().log(Level.WARNING, "Something went wrong while handling incoming message", e);
            }
        }
    }

    public void onI(String[] args) {
        // TODO: Call message history event
    }

    public void onB(String[] args) {
        room.getEventManager().callEvent(new MessageReceiveEvent(room, parseMessage(args)));
    }

    public void onOk(String[] args) {
        // TODO: Store moderators
    }

    public void onInited(String[] args) {
        room.sendCommand("g_participants", "start");
        room.sendCommand("getpremium", "1");
        room.sendCommand("blocklist", "block", "", "next", "500");
        room.sendCommand("blocklist", "unblock", "", "next", "500");
        room.getEventManager().callEvent(new ConnectEvent(room));
    }

    public void onGParticipants(String[] args) {
        String rawArgs = Joiner.on(':').join(args);
        String[] parts = rawArgs.split(";");
        for(String data : parts) {
            String[] subargs = data.split(":");
            String name = subargs[3].toLowerCase();
            if(name.equalsIgnoreCase("none")) {
                continue;
            }
            User user = new User(subargs[0], name, null);
            room.addUser(user);
        }
    }

    public void onParticipant(String[] args) {
        String name = args[3].toLowerCase();
        if(name.equalsIgnoreCase("none")) {
            return;
        }
        if(args[0].equalsIgnoreCase("0")) { // Leave
            User user = room.findUser(name);
            if(user != null) {
                room.removeUser(user);
                room.getEventManager().callEvent(new UserLeaveEvent(room, user));
            }
        } else { // Join
            User user = new User(args[1], name, null);
            if(room.getUserList().contains(user)) {
                user = room.findUser(name);
                user.setSessionId(args[1]);
            } else {
                room.addUser(user);
                room.getEventManager().callEvent(new UserJoinEvent(room, user));
            }
        }
    }

    public void onN(String[] args) {
        int count = new BigInteger(args[0], 16).intValue();
        // TODO: User count change
    }

    private Message parseMessage(String[] args) {
        StringBuilder builder = new StringBuilder();
        for(int i = 9; i < args.length; i++) {
            builder.append(":").append(args[i]);
        }
        String rawMessage = builder.toString().substring(1);

        // Find font and anon id
        String name = args[1];
        Matcher fontMatcher = FONT_PATTERN.matcher(rawMessage);
        Matcher idMatcher = ID_PATTERN.matcher(rawMessage);
        String font = fontMatcher.find() ? fontMatcher.group(1).trim() : "";
        String nTag = idMatcher.find() ? idMatcher.group(1).trim() : "";
        if(name.isEmpty()) {
            name = "#" + args[2];
            if(name.equals("#")) {
                name = "!anon" + nTag;
            }
        }

        User user = parseUser(name);
        user.setUid(args[3]);
        if(user.getSessionId() != "UNKNOWN") {
            user.setNameColor(new RGBColor(nTag));
        }

        String text = rawMessage.replaceAll("<.*?>", "").replace("&lt", "<").replace("&gt", ">").replace("&quot", "\"").replace("&apos", "'").replace("&amp", "&");
        Message message = new Message(text, Font.parseFont(font), user);
        message.setIpAddress(args[6]);
        return message;
    }

    private User parseUser(String name) {
        User user = room.findUser(name);
        return user == null ? new User("UNKNOWN", name, null) : user;
    }
}
