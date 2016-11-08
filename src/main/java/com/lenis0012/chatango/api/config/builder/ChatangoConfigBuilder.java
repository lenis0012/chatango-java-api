package com.lenis0012.chatango.api.config.builder;

import com.lenis0012.chatango.api.config.ChatangoConfig;

public abstract class ChatangoConfigBuilder extends AbstractConfigBuilder<ChatangoConfig> {
    protected boolean threadPerRoom = true;
    protected int threadPoolCount = 1;
    protected boolean autoReconnect = true;
}
