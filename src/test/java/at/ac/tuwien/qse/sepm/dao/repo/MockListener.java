package at.ac.tuwien.qse.sepm.dao.repo;

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

    @Override public void onError(PhotoRepository repository, PersistenceException error) {
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

        private final PersistenceException error;

        public ErrorNotification(PhotoRepository repository, PersistenceException error) {
            super(repository);
            this.error = error;
        }

        public PersistenceException getError() {
            return error;
        }
    }
}
