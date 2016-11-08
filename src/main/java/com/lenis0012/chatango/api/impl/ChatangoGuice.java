package com.lenis0012.chatango.api.impl;

import com.google.inject.AbstractModule;
import com.lenis0012.chatango.api.ChatangoAPI;
import com.lenis0012.chatango.api.impl.io.Authenticator;
import org.apache.hc.client5.http.config.CookieSpecs;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.sync.HttpClient;

public class ChatangoGuice extends AbstractModule {

    @Override
    protected void configure() {
        // Apache HttpComponents
        configureHttpComponents();

        // Modules
        bind(Authenticator.class);

        // Client
        bind(ChatangoAPI.class).to(Engine.class);
    }

    private void configureHttpComponents() {
        CookieStore cookieStore = new BasicCookieStore();
        RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        HttpClient client = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(config)
                .build();

        bind(HttpClient.class).toInstance(client);
        bind(CookieStore.class).toInstance(cookieStore);
    }
}
