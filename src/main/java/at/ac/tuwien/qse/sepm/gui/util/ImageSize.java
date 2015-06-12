package at.ac.tuwien.qse.sepm.gui.util;


public enum ImageSize {
    SMALL,
    MEDIUM,
    ORIGINAL;

    public static int inPixels(ImageSize size) {
        if (size == SMALL) return 100;
        else if (size == MEDIUM) return 200;
        else return 1; // placeholder
    }
}
