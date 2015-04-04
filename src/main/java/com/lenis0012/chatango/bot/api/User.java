package com.lenis0012.chatango.bot.api;

import lombok.Getter;

public class User {
    private String sessionId;
    private final String name;
    private RGBColor nameColor = new RGBColor("000");

    public User(String sessionId, String name) {
        this.sessionId = sessionId;
        this.name = name;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getName() {
        return name;
    }

    public RGBColor getNameColor() {
        return nameColor;
    }

    public void setNameColor(RGBColor nameColor) {
        this.nameColor = nameColor;
    }

    @Override
    public String toString() {
        return "User{" +
                "sessionId='" + sessionId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof User)) {
            return false;
        }
        User u = (User) o;
        return u.sessionId.equalsIgnoreCase(sessionId) && u.name.equalsIgnoreCase(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
