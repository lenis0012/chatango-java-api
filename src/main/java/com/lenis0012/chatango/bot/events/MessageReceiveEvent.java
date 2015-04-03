package com.lenis0012.chatango.bot.events;

import com.lenis0012.chatango.bot.api.Message;
import com.lenis0012.chatango.bot.engine.Room;

public class MessageReceiveEvent extends Event {
    private final Message message;

    public MessageReceiveEvent(Room room, Message message) {
        super(room);
        this.message = message;
    }

    /**
     * Get the message that was received.
     *
     * @return Message
     */
    public Message getMessage() {
        return message;
    }
}
