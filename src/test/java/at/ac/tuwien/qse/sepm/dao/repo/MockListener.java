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

import at.ac.tuwien.qse.sepm.dao.DAOException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Repository listener that stores notifications in lists.
 */
public class MockListener implements PhotoRepository.Listener {

    private final List<PhotoNotification> createNotifications = new LinkedList<>();
    private final List<PhotoNotification> updateNotifications = new LinkedList<>();
    private final List<PhotoNotification> deleteNotifications = new LinkedList<>();
    private final List<ErrorNotification> errorNotifications = new LinkedList<>();

    public List<PhotoNotification> getCreateNotifications() {
        return new ArrayList<>(createNotifications);
    }

    public List<PhotoNotification> getUpdateNotifications() {
        return new ArrayList<>(updateNotifications);
    }

    public List<PhotoNotification> getDeleteNotifications() {
        return new ArrayList<>(deleteNotifications);
    }

    public List<ErrorNotification> getErrorNotifications() {
        return new ArrayList<>(errorNotifications);
    }

    @Override public void onCreate(PhotoRepository repository, Path file) {
        createNotifications.add(new PhotoNotification(repository, file));
    }

    @Override public void onUpdate(PhotoRepository repository, Path file) {
        updateNotifications.add(new PhotoNotification(repository, file));
    }

    @Override public void onDelete(PhotoRepository repository, Path file) {
        deleteNotifications.add(new PhotoNotification(repository, file));
    }

    @Override public void onError(PhotoRepository repository, DAOException error) {
        errorNotifications.add(new ErrorNotification(repository, error));
    }

    public class Notification {

        private final PhotoRepository repository;

        public Notification(PhotoRepository repository) {
            this.repository = repository;
        }

        public PhotoRepository getRepository() {
            return repository;
        }
    }

    public class PhotoNotification extends Notification {

        private final Path file;

        public PhotoNotification(PhotoRepository repository, Path file) {
            super(repository);
            this.file = file;
        }

        public Path getFile() {
            return file;
        }
    }

    public class ErrorNotification extends Notification {

        private final DAOException error;

        public ErrorNotification(PhotoRepository repository, DAOException error) {
            super(repository);
            this.error = error;
        }

        public DAOException getError() {
            return error;
        }
    }
}
