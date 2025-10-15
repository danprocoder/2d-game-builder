package com.gamebuilder.util;

import java.awt.Color;

/** Contains helper functions for color manipulation. */
public class ColorHelper {
    /** Converts a string in the format #rrggbbaa to a Color object */
    public static Color getColorFromHex(String hex) {
        // Remove '#' if present
        String hexColor = hex.startsWith("#") ? hex.substring(1) : hex;
        
        // Parse RGBA components
        int r = Integer.parseInt(hexColor.substring(0, 2), 16);
        int g = Integer.parseInt(hexColor.substring(2, 4), 16);
        int b = Integer.parseInt(hexColor.substring(4, 6), 16);
        int a = hexColor.length() == 8 ? Integer.parseInt(hexColor.substring(6, 8), 16) : 255;
        
        return new Color(r, g, b, a);
    }

    /** Converts a Color object into a hex string in the format #rrggbbaa */
    public static String getHexFromColor(Color color) {
        return String.format("#%02X%02X%02X%02X",
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            color.getAlpha()
        );
    }

    /** Returns a percentage value (0 - 100%) for the color's alpha value */
    public static int getOpacity(Color color) {
        return (int) ((color.getAlpha() / 255.0) * 100);
    }

    /** Sets the color's alpha value based on the percentage input. Returns a new color object. */
    public static Color setOpacity(Color color, int percent) {
        int alpha = (int) ((percent / 100.0) * 255);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
