package com.lenis0012.chatango.bot.api;

public class Font {
    public static final Font DEFAULT = new Font(FontType.ARIAL, 12, new RGBColor("000"));

    private FontType type;
    private int size;
    private RGBColor color;

    public Font(FontType type) {
        this(type, 12);
    }

    public Font(FontType type, int size) {
        this(type, size, new RGBColor("000"));
    }

    public Font(FontType type, int size, RGBColor color) {
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

    public RGBColor getColor() {
        return color;
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
        String sizecolor = components[0].substring(1);

        // Default size and color
        int size = 12;
        String color = "000";

        // Parse size and color
        if(sizecolor.length() > 2) {
            if(sizecolor.length() > 3) {
                size = Integer.parseInt(sizecolor.substring(0, sizecolor.length() - 3)); // Has size
            }
            color = sizecolor.substring(sizecolor.length() - 3); // Has color
        } else {
            size = Integer.parseInt(sizecolor); // Only has size
        }

        // Parse font
        FontType type = FontType.ARIAL;
        try {
            type = FontType.values()[Integer.parseInt(components[1].replace("\"", ""))];
        } catch(NumberFormatException e) {}

        return new Font(type, size, new RGBColor(color));
    }

    public static String encodeFont(Font font) {
        return String.format("<f x%02d%s=\"%s\">", font.size, font.color.getRaw(), font.type.ordinal());
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
