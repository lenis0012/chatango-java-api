package com.lenis0012.chatango.bot;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.lenis0012.chatango.bot.engine.Engine;
import com.lenis0012.chatango.bot.utils.ForwardLogHandler;
import com.lenis0012.chatango.bot.utils.JsonConfig;
import com.lenis0012.chatango.bot.utils.Utils;

import java.math.BigInteger;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Main {
    private static Logger logger;

    public static Logger getLogger() {
        return Main.logger;
    }

    public static void main(String[] args) {
        new Main();
    }

    private final JsonConfig settings;
    private final Engine engine;

    public Main() {
        this.engine = new Engine();
        this.settings = new JsonConfig("settings.json");

        // Initiate logger
        logger = Logger.getGlobal();
        logger.setUseParentHandlers(false);
        for(Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        logger.addHandler(new ForwardLogHandler());

        engine.authenticate( // Grab username and password from configuration file.
                settings.get("account.username", new JsonPrimitive("name")).getAsString(),
                settings.get("account.password", new JsonPrimitive("password")).getAsString());
        if(!engine.getCredentials().isAuthenticated()) {
            settings.save();
            throw new RuntimeException("Invalid login details!");
        }

        // Connect to rooms
        List<String> rooms = Utils.convertList(settings.getList("account.rooms", new JsonArray()), String.class);
        engine.start(rooms);
        settings.save();
    }
}
