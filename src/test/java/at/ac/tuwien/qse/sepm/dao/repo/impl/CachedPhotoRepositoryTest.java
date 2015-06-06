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

    private PhotoRepository getRepository() {
        return new MemoryPhotoRepository(basePath) {
            @Override protected Photo read(Path file, InputStream stream) throws IOException {
                return CachedPhotoRepositoryTest.this.read(file, stream);
            }

            @Override protected void update(Photo photo, OutputStream stream) throws IOException {
                CachedPhotoRepositoryTest.this.update(photo, stream);
            }
        };
    }

    private PhotoCache getCache() {
        return new MemoryPhotoCache();
    }

    @Override protected Path getPhotoFile1() {
        return basePath.resolve("some/file.jpg");
    }

    @Override protected Path getPhotoFile2() {
        return basePath.resolve("other/file.jpg");
    }

    @Test
    public void synchronize_modifiedFiles_updatedInCache() throws PersistenceException {
        PhotoRepository repository = getRepository();
        repository.create(getPhotoFile1(), getPhotoStream1());
        PhotoCache cache = getCache();
        cache.put(getPhoto1());
        repository.update(getPhoto2(getPhotoFile1()));

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        assertEquals(getPhoto2(getPhotoFile1()), cache.read(getPhotoFile1()));
    }

    @Test
    public void synchronize_unmodifiedFiles_notUpdatedInCache() throws PersistenceException {
        // TODO
    }

    @Test
    public void synchronize_deletedFiles_removedFromCache() throws PersistenceException {
        PhotoRepository repository = getRepository();
        repository.create(getPhotoFile1(), getPhotoStream1());
        PhotoCache cache = getCache();
        cache.put(getPhoto1());
        repository.delete(getPhotoFile1());

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        assertNull(cache.check(getPhotoFile1()));
}

    @Test
    public void synchronize_presentFiles_stayInCache() throws PersistenceException {
        // TODO
    }

    @Test
    public void synchronize_createdFiles_addedToCache() throws PersistenceException {
        PhotoRepository repository = getRepository();
        repository.create(getPhotoFile1(), getPhotoStream1());
        PhotoCache cache = getCache();

        CachedPhotoRepository object = new CachedPhotoRepository(repository, cache);
        object.synchronize();
        assertNotNull(cache.check(getPhotoFile1()));
    }
}
