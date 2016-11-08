package com.lenis0012.chatango.api.config.builder;

public abstract class AbstractConfigBuilder<T> {

    /**
     * Construct a new configuration from the specified settings.
     *
     * @return New configuration.
     */
    public abstract T build();
}
