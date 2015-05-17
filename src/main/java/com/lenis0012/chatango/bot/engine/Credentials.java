package com.lenis0012.chatango.bot.engine;

import com.lenis0012.chatango.bot.utils.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class Credentials {
    private static final String LOGIN_URL = "http://chatango.com/login?user_id=%s&password=%s&storecookie=on&checkerrors=yes";
    private final String username;
    private final String password;
    private String authKey = null;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean authenticate() {
        try {
            URL url = new URL(Utils.formatUrl(LOGIN_URL, username, password));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode() != 200) {
                return false;
            }

            Optional<String> authKey = connection.getHeaderFields().get("Set-Cookie").stream().filter(s -> s.startsWith("auth.chatango.com")).map(s -> s.split(";")[0].substring("auth.chatango.com=".length())).findFirst();
            if(authKey.isPresent() && !authKey.get().isEmpty()) {
                this.authKey = authKey.get();
                return true;
            }
            return false;
        } catch(IOException e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthKey() {
        return authKey;
    }

    public boolean isAuthenticated() {
        return authKey != null;
    }
}
