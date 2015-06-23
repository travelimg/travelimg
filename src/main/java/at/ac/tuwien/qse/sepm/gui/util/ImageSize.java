package at.ac.tuwien.qse.sepm.gui.util;


public enum ImageSize {
    SMALL,
    MEDIUM,
    LARGE,
    ORIGINAL;

    public static int inPixels(ImageSize size) {
        if (size == SMALL) return 100;
        else if (size == MEDIUM) return 150;
        else if (size == LARGE) return 300;
        else return 1; // placeholder
    }
}
