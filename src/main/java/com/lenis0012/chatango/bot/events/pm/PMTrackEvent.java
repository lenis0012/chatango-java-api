package com.lenis0012.chatango.bot.events.pm;

public class PMTrackEvent {
    private final String user;
    private final boolean online;
    private final int idle;

    public PMTrackEvent(String user, boolean online, int idle) {
        this.user = user;
        this.online = online;
        this.idle = idle;
    }

    public String getUser() {
        return user;
    }

    public boolean isOnline() {
        return online;
    }

    public int getIdle() {
        return idle;
    }
}
