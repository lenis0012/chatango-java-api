package com.lenis0012.chatango.bot.api;

public class RGBColor {
    private String handle = "000";

    public RGBColor(String handle) {
        this();
        setRaw(handle);
    }

    public RGBColor() {
    }

    public void setRaw(String raw) {
        this.handle = raw;
    }

    public String getRaw() {
        return handle;
    }

    public String encode() {
        return "<n" + handle + "/>";
    }

    /**
     * Set the amount of red in color.
     *
     * @param clr Color code (range 0-F)
     */
    public void setRed(char clr) {
        handle = Character.toString(clr) + handle.substring(1);
    }

    /**
     * Set the amount of green in color.
     *
     * @param clr Color code (range 0-F)
     */
    public void setGreen(char clr) {
        handle = handle.substring(0, 1) + Character.toString(clr) + handle.substring(2);
    }

    /**
     * Set the amount of blue in color.
     *
     * @param clr Color code (range 0-F)
     */
    public void setBlue(char clr) {
        handle = handle.substring(0, 2) + Character.toString(clr);
    }

    /**
     * Set the amount of red in color.
     *
     * @param clr Color code (range 0-15)
     */
    public void setRed(int clr) {
        handle = Integer.toString(clr, 16) + handle.substring(1);
    }

    /**
     * Set the amount of green in color.
     *
     * @param clr Color code (range 0-15)
     */
    public void setGreen(int clr) {
        handle = handle.substring(0, 1) + Integer.toString(clr, 16) + handle.substring(2);
    }

    /**
     * Set blue in color.
     *
     * @param clr Color code (range 0-15)
     */
    public void setBlue(int clr) {
        handle = handle.substring(0, 2) + Integer.toString(clr, 16);
    }
}
