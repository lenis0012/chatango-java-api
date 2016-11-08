package com.lenis0012.chatango.api.exceptions;

/**
 * Exception occurs when credentials are incorrect.
 */
public class AuthException extends Exception {
    /**
     * {@inheritDoc}
     */
    public AuthException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public AuthException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public AuthException(Throwable cause) {
        super(cause);
    }
}
