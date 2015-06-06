package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;
import org.apache.logging.log4j.LogManager;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Repository that uses a cache for better performance.
 *
 * For slow repositories a caching layer can be added through this class. It applies changes
 * synchronously to the cache and asynchronously to the repository. That way the constraints
 * specified by the Repository interface are not violated.
 */
public class CachedPhotoRepository extends RunnablePhotoRepository {

    private final PhotoRepository repository;
    private final PhotoCache cache;

    private final BlockingQueue<Operation> changes = new LinkedBlockingQueue<>();

    public CachedPhotoRepository(PhotoRepository repository, PhotoCache cache) {
        super(LogManager.getLogger());
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

    /**
     * Updates the cache so it has the same content as the repository.
     *
     * @throws PersistenceException failed to perform operation
     */
    public void synchronize() throws PersistenceException {

        Collection<PhotoInfo> cachedContent = getCache().checkAll();
        Collection<PhotoInfo> storedContent = getRepository().checkAll();

        Collection<Path> cachedFiles = cachedContent.stream()
                .map(PhotoInfo::getFile)
                .collect(Collectors.toList());
        Collection<Path> storedFiles = storedContent.stream()
                .map(PhotoInfo::getFile)
                .collect(Collectors.toList());

        // Remove files from the cache that have been removed from the storage.
        for (Path cachedFile : cachedFiles) {
            boolean isStored = storedFiles.contains(cachedFile);
            if (!isStored) {
                getCache().remove(cachedFile);
            }
        }

        // Update files in cache if there is a newer version in the storage.
        for (PhotoInfo cachedInfo : cachedContent) {
            PhotoInfo storedInfo = null;
            for (PhotoInfo info : storedContent) {
                if (info.getFile().equals(cachedInfo.getFile())) {
                    storedInfo = info;
                    break;
                }
            }

            if (storedInfo == null) {
                continue;
            }

            LocalDateTime cachedDate = cachedInfo.getModified();
            LocalDateTime storedDate = storedInfo.getModified();
            boolean isOld = cachedDate.isBefore(storedDate);
            if (isOld) {
                Photo photo = getRepository().read(storedInfo.getFile());
                cache.put(photo);
            }
        }

        // Add files to the cache that have been added to the storage.
        for (Path storedFile : storedFiles) {
            boolean isCached = cachedFiles.contains(storedFile);
            if (!isCached) {
                Photo photo = getRepository().read(storedFile);
                cache.put(photo);
            }
        }
    }

    @Override protected PhotoInfo checkImpl(Path file) throws PersistenceException {
        return getCache().check(file);
    }

    @Override protected Collection<PhotoInfo> checkAllImpl() throws PersistenceException {
        return getCache().checkAll();
    }

    @Override protected Photo readImpl(Path file) throws PersistenceException {
        return getCache().read(file);
    }

    @Override protected Collection<Photo> readAllImpl() throws PersistenceException {
        return getCache().readAll();
    }

    @Override protected void createImpl(Path file, InputStream source) throws PersistenceException {
        getRepository().create(file, source);
        Photo photo = getRepository().read(file);
        getCache().put(photo);
    }

    @Override protected void updateImpl(Photo photo) throws PersistenceException {
        if (getCache().check(photo.getFile()) == null) {
            throw new PhotoNotFoundException(this, photo.getFile());
        }
        getCache().put(photo);
        changes.add(new UpdateOperation(getRepository(), photo));
    }

    @Override protected void deleteImpl(Path file) throws PersistenceException {
        getCache().remove(file);
        changes.add(new DeleteOperation(getRepository(), file));
    }

    private interface Operation {
        public void perform() throws PersistenceException;
    }

    private class UpdateOperation implements Operation {
        private final PhotoRepository repository;
        private final Photo photo;

        public UpdateOperation(PhotoRepository repository, Photo photo) {
            this.repository = repository;
            this.photo = photo;
        }

        @Override public void perform() throws PersistenceException {
            repository.update(photo);
        }
    }

    private class DeleteOperation implements Operation {
        private final PhotoRepository repository;
        private final Path file;

        public DeleteOperation(PhotoRepository repository, Path file) {
            this.repository = repository;
            this.file = file;
        }

        @Override public void perform() throws PersistenceException {
            repository.delete(file);
        }
    }
}
