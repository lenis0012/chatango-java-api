package com.lenis0012.chatango.bot.utils;

public class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable source) {
        super(message, source);
    }
}
