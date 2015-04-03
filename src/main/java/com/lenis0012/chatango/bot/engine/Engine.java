package com.lenis0012.chatango.bot.engine;

import com.lenis0012.chatango.bot.ChatangoAPI;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class Engine {
    private Credentials credentials;

    public void authenticate(String username, String password) {
        this.credentials = new Credentials(username, password);
        credentials.authenticate();
    }

    public void start(List<String> roomNames) {
        try {
            Room room = new Room(roomNames.get(0), this);
            room.login();
            room.start();
        } catch(IOException e) {
            ChatangoAPI.getLogger().log(Level.WARNING, "Failed to connect to room", e);
        }
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
