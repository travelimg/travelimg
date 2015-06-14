package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;
import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Async repository that uses a cache as intermediate storage.
 */
public class CachedPhotoRepository implements AsyncPhotoRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    private final PhotoRepository repository;
    private final PhotoCache cache;

    private final Collection<Listener> listeners = new LinkedList<>();
    private final Collection<AsyncListener> asyncListeners = new LinkedList<>();

    private final Queue<OperationBase> queue = new LinkedList<>();

    public CachedPhotoRepository(PhotoRepository repository, PhotoCache cache) {
        this.repository = repository;
        this.cache = cache;
        repository.addListener(new SourceListener());
    }

    /**
     * Get the repository.
     *
     * @return the underlying repository
     */
    public PhotoRepository getRepository() {
        return repository;
    }

    /**
     * Get the cache.
     *
     * @return cache used
     */
    public PhotoCache getCache() {
        return cache;
    }

    @Override public void synchronize() throws DAOException {
        Collection<Path> cachedIndex = getCache().index();
        for (Path file : cachedIndex) {
            if (!getRepository().contains(file)) {
                getCache().remove(file);
                continue;
            }
            addOperation(new ReadOperation(file));
        }

        Collection<Path> storedIndex = getRepository().index();
        for (Path file : storedIndex) {
            if (!getCache().contains(file)) {
                addOperation(new ReadOperation(file));
            }
        }
    }

    @Override public boolean completeNext() {
        if (queue.isEmpty()) return false;
        OperationBase operation = queue.poll();
        LOGGER.debug("completing next operation {}", operation);

        try {
            operation.perform();
        } catch (DAOException ex) {
            LOGGER.error("error while performing operation {}", operation);
            notifyOperationError(operation, ex);
        }

        LOGGER.debug("successfully performed operation {}", operation);
        notifyOperationComplete(operation);
        return !queue.isEmpty();
    }

    @Override public Queue<Operation> getQueue() {
        return new LinkedList<>(queue);
    }

    @Override public void addListener(AsyncListener listener) {
        if (listener == null) throw new IllegalArgumentException();
        asyncListeners.add(listener);
        LOGGER.info("added async listener {}", listener);
    }

    @Override public void removeListener(AsyncListener listener) {
        if (listener == null) throw new IllegalArgumentException();
        asyncListeners.remove(listener);
        LOGGER.info("removed async listener {}", listener);
    }

    @Override public boolean accepts(Path file) throws DAOException {
        return getRepository().accepts(file);
    }

    /**
     * {@inheritDoc}
     *
     * This implementation of create is not asynchronous and will only return after the photo was
     * created in the source repository. This is necessary, since we have to read the photo before
     * we can add it to the cache.
     */
    @Override public void create(Path file, InputStream source) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        if (source == null) throw new IllegalArgumentException();
        LOGGER.debug("creating {}", file);
        getRepository().create(file, source);
        Photo photo = getRepository().read(file);
        getCache().put(photo);
        notifyCreate(file);
        LOGGER.info("created {}", file);
    }

    @Override public void update(Photo photo) throws DAOException {
        LOGGER.debug("updating {}", photo);
        if (!getCache().contains(photo.getFile())) {
            throw new PhotoNotFoundException(this, photo.getFile());
        }
        getCache().put(photo);
        addOperation(new UpdateOperation(photo));
        notifyUpdate(photo.getFile());
        LOGGER.debug("updated {}", photo);
    }

    @Override public void delete(Path file) throws DAOException {
        LOGGER.debug("deleting {}", file);
        getCache().remove(file);
        addOperation(new DeleteOperation(file));
        notifyDelete(file);
        LOGGER.debug("deleted {}", file);
    }

    @Override public void addListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.add(listener);
        LOGGER.info("added listener {}", listener);
    }

    @Override public void removeListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.remove(listener);
        LOGGER.info("removed listener {}", listener);
    }

    @Override public Collection<Path> index() throws DAOException {
        return getCache().index();
    }

    @Override public Photo read(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);
        Photo photo = getCache().read(file);
        LOGGER.info("read {}", photo);
        return photo;
    }

    private void notifyCreate(Path file) {
        listeners.forEach(l -> l.onCreate(this, file));
    }

    private void notifyUpdate(Path file) {
        listeners.forEach(l -> l.onUpdate(this, file));
    }

    private void notifyDelete(Path file) {
        listeners.forEach(l -> l.onDelete(this, file));
    }

    protected void notifyOperationQueue(Operation operation) {
        asyncListeners.forEach(l -> l.onQueue(this, operation));
    }

    protected void notifyOperationComplete(Operation operation) {
        asyncListeners.forEach(l -> l.onComplete(this, operation));
    }

    protected void notifyOperationError(Operation operation, DAOException ex) {
        asyncListeners.forEach(l -> l.onError(this, operation, ex));
    }

    private void addOperation(OperationBase operation) {
        queue.add(operation);
        LOGGER.debug("added operation {}", operation);
        notifyOperationQueue(operation);
    }

    /* listener for source repository */

    private class SourceListener implements Listener {

        @Override public void onCreate(PhotoRepository repository, Path file) {
            // NOTE: Photo was created in the source repository, so load it into the cache.
            addOperation(new ReadOperation(file));
        }

        @Override public void onUpdate(PhotoRepository repository, Path file) {
            // NOTE: Photo was updated in the source repository, so load it into the cache.
            addOperation(new ReadOperation(file));
        }

        @Override public void onDelete(PhotoRepository repository, Path file) {
            // NOTE: Photo was created in the source repository, so remove it from the cache.
            try {
                getCache().remove(file);
                notifyDelete(file);
            } catch (DAOException ex) {
                LOGGER.warn("failed removing file from cache {}", file);
            }
        }

        @Override public void onError(PhotoRepository repository, DAOException error) {

        }
    }

    /* operation implementations */

    private abstract class OperationBase implements Operation {

        protected final Path file;
        protected final Kind kind;

        public OperationBase(Path file, Kind kind) {
            this.file = file;
            this.kind = kind;
        }

        @Override public Path getFile() {
            return file;
        }

        @Override public Kind getKind() {
            return kind;
        }

        public abstract void perform() throws DAOException;

        @Override public String toString() {
            return "OperationBase{" +
                    "kind=" + kind +
                    ", file=" + file +
                    '}';
        }
    }

    private class ReadOperation extends OperationBase {

        public ReadOperation(Path file) {
            super(file, Kind.READ);
        }

        @Override public void perform() throws DAOException {
            boolean updated = cache.contains(file);
            Photo photo = repository.read(getFile());
            cache.put(photo);
            if (updated) {
                notifyUpdate(file);
            } else {
                notifyCreate(file);
            }
        }
    }

    private class UpdateOperation extends OperationBase {

        protected final Photo photo;

        public UpdateOperation(Photo photo) {
            super(photo.getFile(), Kind.UPDATE);
            this.photo = photo;
        }

        @Override public void perform() throws DAOException {
            repository.update(photo);
            notifyUpdate(photo.getFile());
        }
    }

    private class DeleteOperation extends OperationBase {

        public DeleteOperation(Path file) {
            super(file, Kind.DELETE);
        }

        @Override public void perform() throws DAOException {
            repository.delete(file);
            notifyDelete(file);
        }
    }
}
