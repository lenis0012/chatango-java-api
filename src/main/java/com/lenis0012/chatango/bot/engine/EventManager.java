package com.lenis0012.chatango.bot.engine;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lenis0012.chatango.bot.ChatangoAPI;
import com.lenis0012.chatango.bot.api.EventListener;
import com.lenis0012.chatango.bot.events.Event;
import com.lenis0012.chatango.bot.events.EventHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class EventManager {
    private final Map<Class<? extends Event>, Set<EventInfo>> eventMap = Maps.newConcurrentMap();

    public void addListener(EventListener listener) {
        for(Method method : listener.getClass().getMethods()) {
            if(method.isAnnotationPresent(EventHandler.class)) {
                EventInfo info = new EventInfo(listener, method);
                Set<EventInfo> list = eventMap.getOrDefault(method.getParameterTypes()[0], Sets.newConcurrentHashSet());
                list.add(info);
                eventMap.put((Class<? extends Event>) method.getParameterTypes()[0], list);
            }
        }
    }

    public void callEvent(Event event) {
        Set<EventInfo> list = eventMap.get(event.getClass());
        if(list != null) {
            for(EventInfo info : list) {
                try {
                    info.getMethod().invoke(info.getHandle(), event);
                } catch(Exception e) {
                    ChatangoAPI.getLogger().log(Level.WARNING, "Failed to call event", e);
                }
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class EventInfo {
        private final EventListener handle;
        private final Method method;
    }
}
