package com.lenis0012.chatango.api.impl.io;

import com.lenis0012.chatango.api.Credentials;
import com.lenis0012.chatango.api.exceptions.AuthException;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.methods.HttpGet;
import org.apache.hc.client5.http.sync.HttpClient;
import org.apache.hc.client5.http.utils.URIBuilder;
import org.apache.hc.core5.http.HttpResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.lenis0012.chatango.api.impl.io.HttpConstants.*;

@Singleton
public class Authenticator {
    private final HttpClient httpClient;
    private final CookieStore cookieStore;

    @Inject
    public Authenticator(HttpClient httpClient, CookieStore cookieStore) {
        this.httpClient = httpClient;
        this.cookieStore = cookieStore;
    }

    public void authenticate(Credentials credentials) throws AuthException {
        try {
            URI uri = makeURI(credentials);
            HttpGet get = new HttpGet(uri);
            HttpResponse response = httpClient.execute(get);
            int code = response.getCode();
            Optional<Cookie> authCookie = cookieStore.getCookies().stream()
                    .filter(c -> c.getName().equalsIgnoreCase("auth.chatango.com"))
                    .findFirst();
            if(authCookie.isPresent()) {
                credentials.authKey(authCookie.get().getValue());
            } else {
                throw new AuthException("Login failed, invalid username/password? (code: " + code + ")");
            }
        } catch(Exception e) {
            throw new AuthException(e);
        }
    }

    private URI makeURI(Credentials credentials) throws URISyntaxException {
        return new URIBuilder()
                .setScheme(CHATANGO_PROTOCOL)
                .setHost(CHATANGO_HOST)
                .setPath(CHATANGO_PATH_LOGIN)
                .setParameter("user_id", credentials.getUsername())
                .setParameter("password", credentials.getPassword())
                .setParameter("storecookie", "on")
                .setParameter("checkerrors", "yes")
                .build();
    }
}
