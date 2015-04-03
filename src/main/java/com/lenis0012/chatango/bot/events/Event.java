package com.lenis0012.chatango.bot.events;

import com.lenis0012.chatango.bot.engine.Room;

public abstract class Event {
    protected final Room room;

    public Event(Room room) {
        this.room = room;
    }

    /**
     * Get the room where the event occurred.
     *
     * @return Room
     */
    public Room getRoom() {
        return room;
    }
}
