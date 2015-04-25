package com.lenis0012.chatango.bot.api;

import com.google.common.collect.Sets;
import com.lenis0012.chatango.bot.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class User {
    private static String PROFILE_URL = "http://ust.chatango.com/profileimg/%s/%s/%s/mod1.xml";
    private final Set<String> tags = Sets.newConcurrentHashSet();
    private String sessionId;
    private final String name;
    private RGBColor nameColor = new RGBColor("000");
    private String uid;

    private String gender = "?";
    private Date birth = null;
    private String country = "?";

    public User(final String sessionId, final String name, String uid) {
        this.sessionId = sessionId;
        this.name = name;
        this.uid = uid;
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Utils.formatUrl(PROFILE_URL, name.substring(0, 1), name.substring(1, 2), name));
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = dbf.newDocumentBuilder();
                    Document document = builder.parse(new BufferedInputStream(url.openStream()));
                    Element root = document.getDocumentElement();
                    if(root.getElementsByTagName("s").getLength() > 0) {
                        gender = root.getElementsByTagName("s").item(0).getTextContent();
                    } if(root.getElementsByTagName("b").getLength() > 0) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        birth = format.parse(root.getElementsByTagName("b").item(0).getTextContent());
                    } if(root.getElementsByTagName("l").getLength() > 0) {
                        country = root.getElementsByTagName("l").item(0).getTextContent();
                    }
                } catch (Exception e) {
                }
            }
        }.start();
    }

    public String getGender() {
        return gender;
    }

    public Date getBirth() {
        return birth;
    }

    public String getCountry() {
        return country;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
