package com.lenis0012.chatango.bot.engine;

import com.google.common.collect.Maps;
import com.lenis0012.chatango.bot.ChatangoAPI;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Engine {
    private final Map<String, Room> rooms = Maps.newConcurrentMap();
    private PMManager pmManager;
    private Credentials credentials;

    public void authenticate(String username, String password) {
        this.credentials = new Credentials(username, password);
        credentials.authenticate();
    }

    public void start(List<String> roomNames) {
        this.pmManager = new PMManager(this);
        for(String roomName : roomNames) {
            try {
                Room room = new Room(roomName, this);
                rooms.put(roomName, room);
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
