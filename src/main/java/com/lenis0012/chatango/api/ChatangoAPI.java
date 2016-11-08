package com.lenis0012.chatango.api;

import com.lenis0012.chatango.api.exceptions.AuthException;

public interface ChatangoAPI {

    void authenticate(String username, String password) throws AuthException;

    void authenticate(Credentials credentials) throws AuthException;

    Credentials getCredentials();

    Room connect(String roomName);
}
