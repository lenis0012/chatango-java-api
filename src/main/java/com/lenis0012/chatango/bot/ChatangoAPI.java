package com.lenis0012.chatango.bot;

import com.lenis0012.chatango.bot.engine.Engine;
import com.lenis0012.chatango.bot.utils.AuthException;
import com.lenis0012.chatango.bot.utils.ForwardLogHandler;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class ChatangoAPI {
    private static final Logger logger;

    public static Logger getLogger() {
        return ChatangoAPI.logger;
    }

    static {
        // Initiate logger
        logger = Logger.getGlobal();
        logger.setUseParentHandlers(false);
        for(Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        logger.addHandler(new ForwardLogHandler());
    }

    public static Engine startBot(String username, String password, List<String> rooms) throws AuthException {
        Engine engine = new Engine();
        engine.authenticate(username, password);
        if(!engine.getCredentials().isAuthenticated()) {
            throw new AuthException("Invalid credentials!");
        }

        engine.start(rooms);
        return engine;
    }
}
