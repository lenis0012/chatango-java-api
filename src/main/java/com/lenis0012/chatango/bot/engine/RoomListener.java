package com.lenis0012.chatango.bot.engine;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import com.lenis0012.chatango.bot.Main;
import com.lenis0012.chatango.bot.api.Font;
import com.lenis0012.chatango.bot.api.Message;
import com.lenis0012.chatango.bot.api.User;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        Method method = methods.get(name);
        if(method != null) {
            try {
                method.invoke(this, (Object) args);
            } catch(Exception e) {
                Main.getLogger().log(Level.WARNING, "Something went wrong while handling incoming message", e);
            }
        }
    }

    public void onI(String[] args) {
        StringBuilder builder = new StringBuilder();
        for(int i = 9; i < args.length; i++) {
            builder.append(args[i]);
        }
        String rawMessage = builder.toString();

        // Find font and anon id
        Matcher fontMatcher = FONT_PATTERN.matcher(rawMessage);
        Matcher idMatcher = ID_PATTERN.matcher(rawMessage);
        String font = fontMatcher.find() ? fontMatcher.group(1).trim() : "";
        String nTag = idMatcher.find() ? idMatcher.group(1).trim() : "";
        String name = args[1].isEmpty() ? "Anon" + nTag : args[1];

        String text = rawMessage.replaceAll("<.*?>", "").replace("&lt", "<").replace("&gt", ">").replace("&quot", "\"").replace("&apos", "'").replace("&amp", "&");
        Message message = new Message(text, Font.parseFont(font), new User(name));
        room.onMessage(message);
    }

    public void onOk(String[] args) {
        Main.getLogger().log(Level.INFO, Arrays.asList(args).toString());
        // TODO: Store moderators
    }
}
