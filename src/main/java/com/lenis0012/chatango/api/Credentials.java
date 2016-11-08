package com.lenis0012.chatango.api;

/**
 * Authentication credentials used to log in to chatango.
 */
public class Credentials {
    private final String username;
    private final String password;

    /**
     * Constructs new credentials with username & password authentication.
     *
     * @param username the username used to log in with.
     * @param password the password used to log in with.
     */
    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Get the username used for login from these credentials.
     *
     * @return Username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the password used for login from these credentials.
     *
     * @return Password
     */
    public String getPassword() {
        return password;
    }
}
