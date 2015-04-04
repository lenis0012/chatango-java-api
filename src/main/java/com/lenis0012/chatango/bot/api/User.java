package com.lenis0012.chatango.bot.api;

import com.google.common.collect.Sets;

import java.util.Set;

public class User {
    private final Set<String> tags = Sets.newConcurrentHashSet();
    private String sessionId;
    private final String name;
    private RGBColor nameColor = new RGBColor("000");

    public User(String sessionId, String name) {
        this.sessionId = sessionId;
        this.name = name;
    }

    /**
     * Set user's session id.
     *
     * @param sessionId Session id of user
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Get session id of user.
     *
     * @return User's session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Get name of user.
     *
     * @return Name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Get the name color of the user.
     *
     * @return Color (RGB)
     */
    public RGBColor getNameColor() {
        return nameColor;
    }

    /**
     * Set the name color of the user.
     *
     * @param nameColor Color (RGB)
     */
    public void setNameColor(RGBColor nameColor) {
        this.nameColor = nameColor;
    }

    /**
     * Add an extra data tag to user.
     *
     * @param tag Tag name
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Check if user has an extra data tag.
     *
     * @param tag Tag name
     * @return Whether or not user has the desired tag
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    /**
     * Remove extra tag from user.
     *
     * @param tag Tag name
     * @return Whether or not the user had the tag
     */
    public boolean removetag(String tag) {
        return tags.remove(tag);
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
