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
import java.util.stream.Collectors;

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
            synchronize(file);
        }

        Collection<Path> storedIndex = getRepository().index();
        for (Path file : storedIndex) {
            synchronize(file);
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
        LOGGER.info("created {}", file);
    }

    @Override public void update(Photo photo) throws PersistenceException {
        LOGGER.debug("updating {}", photo);
        if (getCache().check(photo.getFile()) == null) {
            throw new PhotoNotFoundException(this, photo.getFile());
        }
        getCache().put(photo);
        queue.add(new UpdateOperation(getRepository(), photo));
        LOGGER.debug("updated {}", photo);
    }

    @Override public void delete(Path file) throws PersistenceException {
        LOGGER.debug("deleting {}", file);
        getCache().remove(file);
        queue.add(new DeleteOperation(getRepository(), file));
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

    @Override public PhotoInfo check(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("checking {}", file);
        PhotoInfo info = getCache().check(file);
        LOGGER.info("checked {}", info);
        return info;
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

    private void synchronize(Path file) throws PersistenceException {
        boolean sourceExists = getRepository().contains(file);
        boolean cachedExists = getCache().contains(file);

        if (cachedExists && sourceExists) {
            LocalDateTime cachedDate = getCache().check(file).getModified();
            LocalDateTime storedDate = getRepository().check(file).getModified();
            if (cachedDate.isBefore(storedDate)) {
                addOperation(new ReadOperation(this, getCache(), file));
            }
        } else if (cachedExists) {
            getCache().remove(file);
        } else if (sourceExists) {
            addOperation(new ReadOperation(this, getCache(), file));
        }
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
            return null;
        }

        @Override public Kind getKind() {
            return null;
        }

        public abstract void perform() throws PersistenceException;

        @Override public String toString() {
            return "OperationBase{" +
                    "kind=" + kind +
                    ", file=" + file +
                    '}';
        }
    }

    class ReadOperation extends OperationBase {

        protected final PhotoCache cache;

        public ReadOperation(PhotoRepository repository, PhotoCache cache, Path file) {
            super(repository, file, Kind.READ);
            this.cache = cache;
        }

        @Override public void perform() throws PersistenceException {
            Photo photo = repository.read(getFile());
            cache.put(photo);
        }
    }

    class CreateOperation extends OperationBase {

        protected final InputStream source;

        public CreateOperation(PhotoRepository repository, Path file, InputStream source) {
            super(repository, file, Kind.CREATE);
            this.source = source;
        }

        @Override public void perform() throws PersistenceException {
            repository.create(file, source);
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
