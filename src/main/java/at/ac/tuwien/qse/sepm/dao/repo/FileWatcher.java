package at.ac.tuwien.qse.sepm.dao.repo;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
     * Return the workspace directories. //TODO remove after DB implementation
     * @return workspace directories
     */
    Collection<Path> getDirectories();

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
