package com.lenis0012.chatango.bot;

import com.lenis0012.chatango.bot.engine.Engine;
import com.lenis0012.chatango.bot.utils.AuthException;
import com.lenis0012.chatango.bot.utils.ForwardLogHandler;
import com.lenis0012.chatango.bot.utils.RegistrationException;
import com.lenis0012.chatango.bot.utils.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public static void createAccount(String email, String username, String password, String group) throws RegistrationException {
        try {
            URL url = new URL("http://scripts.st.chatango.com/signuptag");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setDoOutput(true);

            // Write params
            String paramsFormat = "email=%s&" +
                    "login=%s&" +
                    "password=%s&" +
                    "password_confirm=%s&" +
                    "checkerrors=yes&" +
                    "group_url=%s";
            String params = Utils.formatUrl(paramsFormat, email, username, password, password, group);
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(params);
            output.flush();
            output.close();

            // Read response
            int code = connection.getResponseCode();
            if(code == 200) {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if(line.contains("<div id=errorbox>")) {
                        String msg = reader.readLine().trim();
                        reader.close();
                        throw new RegistrationException(msg);
                    }
                }
                reader.close();
            } else {
                throw new RegistrationException("Invalid response code: " + code);
            }
        } catch(IOException e) {
            throw new RegistrationException("Failed post or handle web request.", e);
        }
    }
}
