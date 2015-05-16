package com.lenis0012.chatango.bot.events.pm;

public class PMMessageEvent {
    private final String user;
    private final String message;

    public PMMessageEvent(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
