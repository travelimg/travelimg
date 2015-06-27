package at.ac.tuwien.qse.sepm.gui.controller;

/**
 * GUI controller through which various top level application actions can be initiated.
 */
public interface Menu {

    /**
     * Add a listener.
     *
     * @param listener listener to be added
     */
    void addListener(Listener listener);

    /**
     * Remove a listener.
     *
     * @param listener listener to be removed
     */
    void removeListener(Listener listener);

    /**
     * Set the number of grid pages that can be accessed via the menu.
     *
     * @param pageCount number of pages
     */
    void setPageCount(int pageCount);

    /**
     * Get the index of the current page.
     *
     * This index is non-negative and always less than the page count.
     *
     * @return index of the page
     */
    int getCurrentPage();

    interface Listener {

        /**
         * Notify the listener that the user wants to present photos.
         *
         * @param sender menu instance sending the notification
         */
        default void onPresent(Menu sender) { }

        /**
         * Notify the listener that the user wants to import photos from flickr.
         *
         * @param sender menu instance sending the notification
         */
        default void onFlickr(Menu sender) { }

        /**
         * Notify the listener that the user wants to manager journeys.
         *
         * @param sender menu instance sending the notification
         */
        default void onJourney(Menu sender) { }

        /**
         * Notify the listener that the user wants to delete photos.
         *
         * @param sender menu instance sending the notification
         */
        default void onDelete(Menu sender) { }

        /**
         * Notify the listener that the user wants to export photos.
         *
         * @param sender menu instance sending the notification
         */
        default void onExport(Menu sender) { }

        /**
         * Notify the listener that the user switched to another page.
         *
         * @param sender menu instance sending the notification
         */
        default void onPageSwitch(Menu sender) { }
    }
}
