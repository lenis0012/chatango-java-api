package com.lenis0012.chatango.bot.engine;

import com.lenis0012.chatango.bot.ChatangoAPI;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Engine {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private PMManager pmManager;
    private Credentials credentials;

    public void authenticate(String username, String password) {
        this.credentials = new Credentials(username, password);
        credentials.authenticate();
    }

    public void init(List<String> roomNames) {
        this.pmManager = new PMManager(this);
        for(String roomName : roomNames) {
            Room room = new Room(roomName, this);
            rooms.put(roomName, room);
        }
    }

    public void start() {
        for(Room room : rooms.values()) {
            try {
                room.connect();
                room.login();
            } catch(IOException e) {
                ChatangoAPI.getLogger().log(Level.WARNING, "Failed to connect to room", e);
            }
        }
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public Collection<Room> getRooms() {
        return Collections.unmodifiableCollection(rooms.values());
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }

    public PMManager getPmManager() {
        return pmManager;
    }
}
