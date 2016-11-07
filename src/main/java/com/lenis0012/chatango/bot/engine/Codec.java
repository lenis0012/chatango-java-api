package com.lenis0012.chatango.bot.engine;

import java.io.IOException;

public interface Codec {

    void onMessageReceive(String message);

    void connect(String host, int port) throws IOException;

    void sendCommand(String... args);
}
