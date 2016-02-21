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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MockAsyncListener implements AsyncPhotoRepository.AsyncListener {

    private final List<OperationNotification> queueNotifications = new LinkedList<>();
    private final List<OperationNotification> completeNotifications = new LinkedList<>();
    private final List<ErrorNotification> errorNotifications = new LinkedList<>();

    public List<OperationNotification> getQueueNotifications() {
        return new ArrayList<>(queueNotifications);
    }

    public List<OperationNotification> getCompleteNotifications() {
        return new ArrayList<>(completeNotifications);
    }

    public List<ErrorNotification> getErrorNotifications() {
        return new ArrayList<>(errorNotifications);
    }

    @Override public void onQueue(AsyncPhotoRepository repository, Operation operation) {
        queueNotifications.add(new OperationNotification(repository, operation));
    }

    @Override public void onComplete(AsyncPhotoRepository repository, Operation operation) {
        completeNotifications.add(new OperationNotification(repository, operation));
    }

    @Override public void onError(AsyncPhotoRepository repository, Operation operation, DAOException error) {
        errorNotifications.add(new ErrorNotification(repository, error));
    }

    public class Notification {
        private final AsyncPhotoRepository repository;

        public Notification(AsyncPhotoRepository repository) {
            this.repository = repository;
        }

        public AsyncPhotoRepository getRepository() {
            return repository;
        }
    }

    public class OperationNotification extends Notification {

        private final Operation operation;

        public OperationNotification(AsyncPhotoRepository repository, Operation operation) {
            super(repository);
            this.operation = operation;
        }

        public Operation getOperation() {
            return operation;
        }
    }

    public class ErrorNotification extends Notification {

        private final DAOException error;

        public ErrorNotification(AsyncPhotoRepository repository, DAOException error) {
            super(repository);
            this.error = error;
        }

        public DAOException getError() {
            return error;
        }
    }
}
