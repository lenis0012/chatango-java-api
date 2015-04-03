package com.lenis0012.chatango.bot.engine;

import com.google.common.collect.Maps;
import com.lenis0012.chatango.bot.ChatangoAPI;
import com.lenis0012.chatango.bot.api.Font;
import com.lenis0012.chatango.bot.api.Message;
import com.lenis0012.chatango.bot.api.User;

import java.lang.reflect.Method;
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
        Method method = methods.get(name);
        if(method != null) {
            try {
                method.invoke(this, (Object) args);
            } catch(Exception e) {
                ChatangoAPI.getLogger().log(Level.WARNING, "Something went wrong while handling incoming message", e);
            }
        }
    }

    public void onI(String[] args) {
        StringBuilder builder = new StringBuilder();
        for(int i = 9; i < args.length; i++) {
            builder.append(":").append(args[i]);
        }
        String rawMessage = builder.toString().substring(1);

        // Find font and anon id
        Matcher fontMatcher = FONT_PATTERN.matcher(rawMessage);
        Matcher idMatcher = ID_PATTERN.matcher(rawMessage);
        String font = fontMatcher.find() ? fontMatcher.group(1).trim() : "";
        String nTag = idMatcher.find() ? idMatcher.group(1).trim() : "";
        String name = args[1].isEmpty() ? "Anon" + nTag : args[1];

        String text = rawMessage.replaceAll("<.*?>", "").replace("&lt", "<").replace("&gt", ">").replace("&quot", "\"").replace("&apos", "'").replace("&amp", "&");
        Message message = new Message(text, Font.parseFont(font), new User(name));
    }

    public void onB(String[] args) {
        // TODO: Call message event
    }

    public void onOk(String[] args) {
        // TODO: Store moderators
    }

    public void onInited(String[] args) {
        room.sendCommand("g_participants", "start");
        room.sendCommand("getpremium", "1");
        room.sendCommand("blocklist", "block", "", "next", "500");
        room.sendCommand("blocklist", "unblock", "", "next", "500");
    }
}
