package com.lenis0012.chatango.bot.api;

public enum Channel {
    DEFAULT(0x00), // 0
    BLUE(0x800), // 2048
    RED(0x100); // 256

    private final int id;

    private Channel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
