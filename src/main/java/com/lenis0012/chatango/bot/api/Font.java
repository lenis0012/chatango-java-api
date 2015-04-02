package com.lenis0012.chatango.bot.api;

public class Font {
    public static final Font DEFAULT = new Font(FontType.ARIAL, 12, "000");

    private FontType type;
    private int size;
    private String color;

    public Font(FontType type, int size, String color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    public FontType getType() {
        return type;
    }

    public void setType(FontType type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = Math.min(22, Math.max(9, size));
    }

    public String getColor() {
        return color;
    }

    public boolean setColor(String color) {
        if(color.length() == 3 && color.replaceAll("[^A-Za-z0-9]", "").equals(color)) {
            this.color = color;
            return true;
        }
        return false;
    }

    @Override
    public Font clone() {
        return new Font(type, size, color);
    }

    public static Font parseFont(String raw) {
        if(raw.isEmpty()) {
            return DEFAULT.clone();
        }
        String[] components = raw.split("=");
        int size = Integer.parseInt(components[0].substring(1, 3));
        String color = components[0].substring(3, 6);
        FontType type = FontType.values()[Integer.parseInt(components[1].replace("\"", ""))];
        return new Font(type, size, color);
    }

    public static enum FontType {
        ARIAL,
        COMIC,
        GEORGIA,
        HANDWRITING,
        IMPACT,
        PALATINO,
        PAPYRUS,
        TIMES,
        TYPEWRITER;
    }
}
