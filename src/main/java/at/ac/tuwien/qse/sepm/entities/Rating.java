package at.ac.tuwien.qse.sepm.entities;

public enum Rating {

    /**
     * Photo has not yet been rated.
     */
    NONE,

    /**
     * Photo is of low quality.
     */
    BAD,

    /**
     * Photo was rated and is acceptable.
     */
    NEUTRAL,

    /**
     * Photo is of high quality.
     */
    GOOD;

    /**
     * Converts an integer to a PhotoRating, by interpreting it as the ordinal number of the enum
     * value. If the integer exceeds the range of the ordinals the return value defaults to
     * PhotoRating.NONE.
     *
     * @param index
     * @return enum value
     */
    public static Rating from(int index) {
        if (index < 0 || Rating.values().length <= index) {
            return Rating.NONE;
        }
        return Rating.values()[index];
    }
}
