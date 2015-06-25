package at.ac.tuwien.qse.sepm.gui.util;


public enum ImageSize {
    SMALL,
    MEDIUM,
    LARGE,
    ORIGINAL;

    public int pixels() {
        switch (this) {
            case SMALL: return 100;
            case MEDIUM: return 150;
            case LARGE: return 300;
            default: return 1; // placeholder
        }
    }
}
