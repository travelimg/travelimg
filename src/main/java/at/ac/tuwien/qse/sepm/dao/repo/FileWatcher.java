package at.ac.tuwien.qse.sepm.dao.repo;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

/**
 * Class for observing files in one or more directories.
 *
 * Every file goes through a complete lifecycle. That means an update or delete notification
 * for a file is only sent, if there was an initial create notification during the lifetime
 * of the watcher.
 */
public interface FileWatcher {

    /**
     * Check whether the file would be watched by the watcher.
     *
     * @param file file to check
     * @return true if the file would be watched, else false
     */
    boolean recognizes(Path file);

    /**
     * Check whether the file is currently watched.
     *
     * @param file file to check
     * @return true if the file is being watched, otherwise false
     */
    boolean watches(Path file);

    /**
     * Get all files in the registered directories.
     *
     * @return set of files
     */
    Collection<Path> index();

    /**
     * Registers a directory that should be observed.
     *
     * Files that are already in the directory will trigger create notifications.
     *
     * @param directory directory to register
     */
    void register(Path directory);

    /**
     * Removes a registered directory, so that it is no longer observed.
     *
     * Files that are in the removed directory trigger delete notifications.
     *
     * @param directory directory to unregister
     */
    void unregister(Path directory);

    /**
     * Add a listener that is notified about file operations.
     *
     * @param listener listener that should be added
     */
    void addListener(Listener listener);

    /**
     * Remove a listener from the repository.
     *
     * @param listener listener that should be removed
     */
    void removeListener(Listener listener);

    /**
     * Listener that processes file operation notifications.
     */
    public interface Listener {

        /**
         * Notifies the listener that a file was created.
         *
         * @param file file that was created
         */
        void onCreate(FileWatcher watcher, Path file);

        /**
         * Notifies the listener that a file was updated.
         *
         * @param file file that was updated
         */
        void onUpdate(FileWatcher watcher, Path file);

        /**
         * Notifies the listener that a file was deleted.
         *
         * @param file file that was deleted
         */
        void onDelete(FileWatcher watcher, Path file);
    }
}
