package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CachedPhotoRepositoryTest extends AsyncPhotoRepositoryTest {

    private static final Path PREFIX = Paths.get("test/path");

    protected PhotoRepository repository;
    protected PhotoCache cache;
    protected CachedPhotoRepository object;

    @Before
    public void setUp() {
        repository = new MemoryPhotoRepository(getContext().getSerializer(), PREFIX);
        cache = new MemoryPhotoCache();
        object = new CachedPhotoRepository(repository, cache);
    }

    @Override protected CachedPhotoRepository getObject() {
        return object;
    }

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getFile1() {
                return PREFIX.resolve("some/file.jpg");
            }

            @Override public Path getFile2() {
                return PREFIX.resolve("other/photo.jpg");
            }

            @Override public Path getUnacceptedPath() {
                return Paths.get("somewhere/outside/test/path.jpg");
            }
        };
    }

    private PhotoRepository getRepository() {
        return repository;
    }

    private PhotoCache getCache() {
        return cache;
    }

    @Test
    public void synchronize_modifiedFiles_addedToQueue() throws DAOException {
        PhotoRepository repository = getRepository();
        Path file = getContext().getFile1();
        repository.create(file, getContext().getStream1());
        PhotoCache cache = getCache();
        cache.put(getContext().getPhoto1());
        Photo modified = getContext().getModified1();
        repository.update(modified);

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        while (object.completeNext());
        assertEquals(modified, cache.read(file));
    }

    @Test
    public void synchronize_modifiedFiles_updatedInCache() throws DAOException {
        PhotoRepository repository = getRepository();
        Path file = getContext().getFile1();
        repository.create(file, getContext().getStream1());
        PhotoCache cache = getCache();
        cache.put(getContext().getPhoto1());
        Photo modified = getContext().getModified1();
        repository.update(modified);

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        while (object.completeNext());
        assertEquals(modified, cache.read(file));
    }

    @Test
    public void synchronize_unmodifiedFiles_notUpdatedInCache() throws DAOException {
        // TODO
    }

    @Test
    public void synchronize_deletedFiles_removedFromCache() throws DAOException {
        PhotoRepository repository = getRepository();
        Path file = getContext().getFile1();
        repository.create(file, getContext().getStream1());
        PhotoCache cache = getCache();
        cache.put(getContext().getPhoto1());
        repository.delete(file);

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        assertFalse(cache.contains(file));
    }

    @Test
    public void synchronize_presentFiles_stayInCache() throws DAOException {
        // TODO
    }

    @Test
    public void synchronize_createdFiles_addedToCache() throws DAOException {
        PhotoRepository repository = getRepository();
        Path file = getContext().getFile1();
        repository.create(file, getContext().getStream1());
        PhotoCache cache = getCache();

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        while (object.completeNext());
        assertTrue(cache.contains(file));
    }
}
