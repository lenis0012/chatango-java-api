package com.lenis0012.chatango.api.config;

import com.lenis0012.chatango.api.config.builder.ChatangoConfigBuilder;

public class ChatangoConfig {
    private final boolean threadPerRoom;
    private final int threadPoolCount;
    private final boolean autoReconnect;

    public static ChatangoConfigBuilder builder() {
        return new ChatangoConfigBuilder() {
            @Override
            public ChatangoConfig build() {
                return new ChatangoConfig(threadPerRoom, threadPoolCount, autoReconnect);
            }
        };
    }

    private ChatangoConfig(boolean threadPerRoom, int threadPoolCount, boolean autoReconnect) {
        this.threadPerRoom = threadPerRoom;
        this.threadPoolCount = threadPoolCount;
        this.autoReconnect = autoReconnect;
    }

    public boolean threadPerRoom() {
        return threadPerRoom;
    }

    public int threadPoolCount() {
        return threadPoolCount;
    }

    public boolean autoReconnect() {
        return autoReconnect;
    }
}
