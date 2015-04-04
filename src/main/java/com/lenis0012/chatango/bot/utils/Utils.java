package com.lenis0012.chatango.bot.utils;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import lombok.SneakyThrows;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class Utils {

    @SneakyThrows(UnsupportedEncodingException.class)
    public static String urlEncode(String url, String... args) {
        StringBuilder builder = new StringBuilder(url).append('?');
        for(String arg : args) {
            arg = URLEncoder.encode(arg, "UTF-8");
            if(builder.charAt(builder.length() - 1) == '=') {
                builder.append(arg).append('&');
            } else {
                builder.append(arg).append('=');
            }
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public static String formatUrl(String url, String... args) {
        String[] newArgs = new String[args.length];
        for(int i = 0; i < args.length; i++) {
            newArgs[i] = URLEncoder.encode(args[i], "UTF-8");
        }
        return String.format(url, newArgs);
    }

    public static <T> List<T> convertList(JsonArray from, Class<T> type) {
        List<T> list = Lists.newArrayList();
        for(int i = 0; i < from.size(); i++) {
            list.add(type.cast(from.get(i).getAsString()));
        }
        return list;
    }

    public static String pythToJav(String txt) {
        for(int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if(c == '_' && txt.length() > i + 2) {
                txt = txt.substring(0, i) + txt.substring(i + 1, i + 2).toUpperCase() + txt.substring(i + 2);
            }
        }
        return txt;
    }
}
