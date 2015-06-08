package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Async repository that uses a cache as intermediate storage.
 */
public class CachedPhotoRepository
        extends RunnablePhotoRepository
        implements AsyncPhotoRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    private final PhotoRepository repository;
    private final PhotoCache cache;

    private final Collection<Listener> listeners = new LinkedList<>();
    private final Collection<AsyncListener> asyncListeners = new LinkedList<>();

    private final BlockingQueue<OperationBase> queue = new LinkedBlockingQueue<>();

    public CachedPhotoRepository(PhotoRepository repository, PhotoCache cache) {
        this.repository = repository;
        this.cache = cache;
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

    @Override public void synchronize() throws PersistenceException {
        Collection<Path> cachedIndex = getCache().index();
        for (Path file : cachedIndex) {
            if (!getRepository().contains(file)) {
                getCache().remove(file);
                continue;
            }
            addOperation(new SyncOperation(this, getCache(), file));
        }
    }

    @Override public void completeNext() {
        OperationBase operation;
        try {
            operation = queue.take();
        } catch (InterruptedException ex) {
            LOGGER.info("run thread interrupted");
            stop();
            return;
        }

        try {
            operation.perform();
        } catch (PersistenceException ex) {
            LOGGER.error("error while performing operation {}", operation);
            notifyOperationError(operation, ex);
        }

        LOGGER.debug("successfully performed operation {}", operation);
        notifyOperationComplete(operation);
    }

    @Override public Queue<Operation> getQueue() {
        return new LinkedList<>(queue);
    }

    @Override public void addListener(AsyncListener listener) {
        if (listener == null) throw new IllegalArgumentException();
        asyncListeners.add(listener);
        LOGGER.info("added listener {}", listener);
    }

    @Override public void removeListener(AsyncListener listener) {
        if (listener == null) throw new IllegalArgumentException();
        asyncListeners.remove(listener);
        LOGGER.info("removed listener {}", listener);
    }

    @Override public boolean accepts(Path file) throws PersistenceException {
        return getRepository().accepts(file);
    }

    @Override public void create(Path file, InputStream source) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        if (source == null) throw new IllegalArgumentException();
        LOGGER.debug("creating {}", file);
        getRepository().create(file, source);
        Photo photo = getRepository().read(file);
        getCache().put(photo);
        listeners.forEach(l -> l.onCreate(repository, file));
        LOGGER.info("created {}", file);
    }

    @Override public void update(Photo photo) throws PersistenceException {
        LOGGER.debug("updating {}", photo);
        if (!getCache().contains(photo.getFile())) {
            throw new PhotoNotFoundException(this, photo.getFile());
        }
        getCache().put(photo);
        addOperation(new UpdateOperation(getRepository(), photo));
        listeners.forEach(l -> l.onUpdate(repository, photo.getFile()));
        LOGGER.debug("updated {}", photo);
    }

    @Override public void delete(Path file) throws PersistenceException {
        LOGGER.debug("deleting {}", file);
        getCache().remove(file);
        addOperation(new DeleteOperation(getRepository(), file));
        listeners.forEach(l -> l.onDelete(repository, file));
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

    @Override public Collection<Path> index() throws PersistenceException {
        return getCache().index();
    }

    @Override public Photo read(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);
        Photo photo = getCache().read(file);
        LOGGER.info("read {}", photo);
        return photo;
    }

    protected void notifyOperationAdd(Operation operation) {
        asyncListeners.forEach(l -> l.onAdd(this, operation));
    }

    protected void notifyOperationComplete(Operation operation) {
        asyncListeners.forEach(l -> l.onComplete(this, operation));
    }

    protected void notifyOperationError(Operation operation, PersistenceException ex) {
        asyncListeners.forEach(l -> l.onError(this, operation, ex));
    }

    private void addOperation(OperationBase operation) {
        queue.add(operation);
        LOGGER.debug("added operation {}", operation);
        notifyOperationAdd(operation);
    }

    /******************************/
    /** Operation Implementations */
    /******************************/

    abstract class OperationBase implements Operation {

        protected final PhotoRepository repository;
        protected final Path file;
        protected final Kind kind;

        public OperationBase(PhotoRepository repository, Path file, Kind kind) {
            this.repository = repository;
            this.file = file;
            this.kind = kind;
        }

        @Override public Path getFile() {
            return file;
        }

        @Override public Kind getKind() {
            return kind;
        }

        public abstract void perform() throws PersistenceException;

        @Override public String toString() {
            return "OperationBase{" +
                    "kind=" + kind +
                    ", file=" + file +
                    '}';
        }
    }

    class SyncOperation extends OperationBase {

        protected final PhotoCache cache;

        public SyncOperation(PhotoRepository repository, PhotoCache cache, Path file) {
            super(repository, file, Kind.READ);
            this.cache = cache;
        }

        @Override public void perform() throws PersistenceException {
            Photo photo = repository.read(getFile());
            cache.put(photo);
        }
    }

    class UpdateOperation extends OperationBase {

        protected final Photo photo;

        public UpdateOperation(PhotoRepository repository, Photo photo) {
            super(repository, photo.getFile(), Kind.UPDATE);
            this.photo = photo;
        }

        @Override public void perform() throws PersistenceException {
            repository.update(photo);
        }
    }

    class DeleteOperation extends OperationBase {

        public DeleteOperation(PhotoRepository repository, Path file) {
            super(repository, file, Kind.DELETE);
        }

        @Override public void perform() throws PersistenceException {
            repository.delete(file);
        }
    }
}
