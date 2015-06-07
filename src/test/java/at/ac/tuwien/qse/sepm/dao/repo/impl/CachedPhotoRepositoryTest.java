package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CachedPhotoRepositoryTest extends PhotoRepositoryTest {

    private final Path basePath = Paths.get("test/path");

    @Override protected CachedPhotoRepository getObject() {
        return new CachedPhotoRepository(getRepository(), getCache());
    }

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getFile1() {
                return basePath.resolve("some/file.jpg");
            }

            @Override public Path getFile2() {
                return basePath.resolve("other/photo.jpg");
            }
        };
    }

    @Override protected void add(PhotoProvider object, Photo photo) throws PersistenceException {
        ((PhotoRepository)object).create(photo.getFile(), getContext().getStream(photo));
    }

    private PhotoRepository getRepository() {
        return new MemoryPhotoRepository(basePath) {
            @Override protected Photo read(Path file, InputStream stream) throws IOException {
                return getContext().read(file, stream);
            }

            @Override protected void update(Photo photo, OutputStream stream) throws IOException {
                getContext().update(photo, stream);
            }
        };
    }

    private PhotoCache getCache() {
        return new MemoryPhotoCache();
    }

    @Test
    public void sychtonize_modifiedFiles_addedToQueue() throws PersistenceException {
        PhotoRepository repository = getRepository();
        Path file = getContext().getFile1();
        repository.create(file, getContext().getStream1());
        PhotoCache cache = getCache();
        cache.put(getContext().getPhoto1());
        Photo modified = getContext().getModified1();
        repository.update(modified);

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        assertEquals(modified, cache.read(file));
    }

    @Test
    public void synchronize_modifiedFiles_updatedInCache() throws PersistenceException {
        PhotoRepository repository = getRepository();
        Path file = getContext().getFile1();
        repository.create(file, getContext().getStream1());
        PhotoCache cache = getCache();
        cache.put(getContext().getPhoto1());
        Photo modified = getContext().getModified1();
        repository.update(modified);

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        assertEquals(modified, cache.read(file));
    }

    @Test
    public void synchronize_unmodifiedFiles_notUpdatedInCache() throws PersistenceException {
        // TODO
    }

    @Test
    public void synchronize_deletedFiles_removedFromCache() throws PersistenceException {
        PhotoRepository repository = getRepository();
        Path file = getContext().getFile1();
        repository.create(file, getContext().getStream1());
        PhotoCache cache = getCache();
        cache.put(getContext().getPhoto1());
        repository.delete(file);

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        assertNull(cache.check(file));
    }

    @Test
    public void synchronize_presentFiles_stayInCache() throws PersistenceException {
        // TODO
    }

    @Test
    public void synchronize_createdFiles_addedToCache() throws PersistenceException {
        PhotoRepository repository = getRepository();
        Path file = getContext().getFile1();
        repository.create(file, getContext().getStream1());
        PhotoCache cache = getCache();

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        assertNotNull(cache.check(file));
    }
}
