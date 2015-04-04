package com.lenis0012.chatango.bot.events;

import com.lenis0012.chatango.bot.api.User;
import com.lenis0012.chatango.bot.engine.Room;

public class UserJoinEvent extends Event {
    private final User user;

    public UserJoinEvent(Room room, User user) {
        super(room);
        this.user = user;
    }

    /**
     * Get the user who connected to the room.
     *
     * @return User
     */
    public User getUser() {
        return user;
    }
}
