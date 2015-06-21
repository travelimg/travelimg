package at.ac.tuwien.qse.sepm.gui.util;

import javafx.scene.paint.Color;

public class ColorUtils {

    /**
     * Convert an integer to a color assuming a 0xrrggbb encoding.
     * @param color The integer from which to derive the color.
     * @return The resulting color object from the given integer.
     */
    public static Color fromInt(int color) {
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;

        return Color.rgb(r, g, b);
    }

    /**
     * Encode a color as an integer using a 0xrrggbb encoding.
     * @param color The color which to convert to an integer.
     * @return The resulting integer representing the color.
     */
    public static int toInt(Color color) {
        int r = (int)(color.getRed() * 255);
        int g = (int)(color.getGreen() * 255);
        int b = (int)(color.getBlue() * 255);

        return (r << 16) + (g << 8) + b;
    }
}
