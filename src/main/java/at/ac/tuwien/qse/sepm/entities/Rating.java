package at.ac.tuwien.qse.sepm.entities;

public enum Rating {

    /**
     * Photo has not yet been rated.
     */
    NONE,

    /**
     * Photo is not good and will be hidden from most views per default.
     */
    HIDDEN,

    /**
     * Photo was rated and is acceptable.
     */
    NEUTRAL,

    /**
     * Photo is especially good and should be highlighted in most views.
     */
    FAVORITE;

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
