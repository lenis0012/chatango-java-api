package com.lenis0012.chatango.bot.events;

import com.lenis0012.chatango.bot.engine.Room;

public class ConnectEvent extends Event {
    public ConnectEvent(Room room) {
        super(room);
    }
}
