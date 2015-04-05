package com.lenis0012.chatango.bot.api;

public enum Badge {
    NONE(0x0), //
    SHIELD(0x40), // 64
    STAFF(0x80); // 128

    private final int id;

    private Badge(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
