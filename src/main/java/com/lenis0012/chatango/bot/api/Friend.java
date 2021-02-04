package com.lenis0012.chatango.bot.api;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Friend {
    private final List<String> messages = Collections.synchronizedList(new ArrayList<>());
    private final String name;
    private boolean online;
    private int idle;
    private long lastOnline;
    private BufferedImage avatar;

    public Friend(String name, boolean online, int idle, long lastOnline) {
        this.name = name;
        this.online = online;
        this.idle = idle;
        this.lastOnline = lastOnline;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getIdle() {
        return idle;
    }

    public void setIdle(int idle) {
        this.idle = idle;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public BufferedImage getAvatar() {
        if(avatar != null) {
            return avatar;
        } else if(name.length() < 2) {
            this.avatar = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
            return avatar;
        }

        try {
            String link = String.format("http://ust.chatango.com/profileimg/%s/%s/%s/thumb.jpg",
                    name.substring(0, 1), name.substring(1, 2), name);
            this.avatar = ImageIO.read(new URL(link));
            return avatar;
        } catch(IOException e) {
            e.printStackTrace();
            return new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        }
    }
}
