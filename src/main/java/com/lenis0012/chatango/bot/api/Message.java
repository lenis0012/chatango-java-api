package com.lenis0012.chatango.bot.api;

public class Message {
    private final String text;
    private Font font;
    private User user;

    public Message(String message) {
        this(message, null);
    }

    public Message(String message, Font font) {
        this(message, font, null);
    }

    public Message(String message, Font font, User user) {
        this.text = message;
        this.font = font;
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
