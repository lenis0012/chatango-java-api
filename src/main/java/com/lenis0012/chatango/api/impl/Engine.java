package com.lenis0012.chatango.api.impl;

import com.lenis0012.chatango.api.ChatangoAPI;
import com.lenis0012.chatango.api.Credentials;
import com.lenis0012.chatango.api.Room;
import com.lenis0012.chatango.api.exceptions.AuthException;
import com.lenis0012.chatango.api.impl.io.Authenticator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Engine implements ChatangoAPI {
    // Dependencies
    private final Authenticator authenticator;

    // Properties
    private Credentials credentials;

    @Inject
    public Engine(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void authenticate(String username, String password) throws AuthException {
        authenticate(new Credentials(username, password));
    }

    @Override
    public void authenticate(Credentials credentials) throws AuthException {
        if(this.credentials != null) throw new IllegalStateException("Already authenticated!");
        authenticator.authenticate(credentials);
        this.credentials = credentials;
    }

    @Override
    public Credentials getCredentials() {
        if(credentials == null) throw new IllegalStateException("Can't get credentials when not authenticated!");
        return credentials;
    }

    @Override
    public Room connect(String roomName) {
        if(credentials == null) throw new IllegalStateException("");
        return null;
    }
}
