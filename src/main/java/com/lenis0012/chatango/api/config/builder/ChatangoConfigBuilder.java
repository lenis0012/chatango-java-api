package com.lenis0012.chatango.api.config.builder;

import com.lenis0012.chatango.api.config.ChatangoConfig;

public abstract class ChatangoConfigBuilder extends AbstractConfigBuilder<ChatangoConfig> {
    protected boolean threadPerRoom = true;
    protected int threadPoolCount = 1;
    protected boolean autoReconnect = true;

    public ChatangoConfigBuilder threadPerRoom(boolean threadPerRoom) {
        this.threadPerRoom = threadPerRoom;
        return this;
    }

    public ChatangoConfigBuilder threadPoolCount(int threadPoolCount) {
        this.threadPoolCount = threadPoolCount;
        return this;
    }

    public ChatangoConfigBuilder autoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }
}
