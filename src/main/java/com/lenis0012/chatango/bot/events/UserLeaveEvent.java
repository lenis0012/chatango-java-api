package com.lenis0012.chatango.bot.events;

import com.lenis0012.chatango.bot.api.User;
import com.lenis0012.chatango.bot.engine.Room;

public class UserLeaveEvent extends Event {
    private final User user;

    public UserLeaveEvent(Room room, User user) {
        super(room);
        this.user = user;
    }

    /**
     * Get user who left.
     *
     * @return User
     */
    public User getUser() {
        return user;
    }
}
