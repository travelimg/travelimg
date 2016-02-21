package at.ac.tuwien.qse.sepm.dao.repo.impl;

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
