package at.ac.tuwien.qse.sepm.dao.repo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MockAsyncListener implements AsyncPhotoRepository.AsyncListener {

    private final List<OperationNotification> addNotifications = new LinkedList<>();
    private final List<OperationNotification> completeNotifications = new LinkedList<>();
    private final List<ErrorNotification> errorNotifications = new LinkedList<>();

    public List<OperationNotification> getAddNotifications() {
        return new ArrayList<>(addNotifications);
    }

    public List<OperationNotification> getCompleteNotifications() {
        return new ArrayList<>(completeNotifications);
    }

    public List<ErrorNotification> getErrorNotifications() {
        return new ArrayList<>(errorNotifications);
    }

    @Override public void onQueue(AsyncPhotoRepository repository, Operation operation) {
        addNotifications.add(new OperationNotification(repository, operation));
    }

    @Override public void onComplete(AsyncPhotoRepository repository, Operation operation) {
        completeNotifications.add(new OperationNotification(repository, operation));
    }

    @Override public void onError(AsyncPhotoRepository repository, Operation operation, PersistenceException error) {
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

        private final PersistenceException error;

        public ErrorNotification(AsyncPhotoRepository repository, PersistenceException error) {
            super(repository);
            this.error = error;
        }

        public PersistenceException getError() {
            return error;
        }
    }
}
