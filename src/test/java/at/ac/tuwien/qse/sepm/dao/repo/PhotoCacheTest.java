package at.ac.tuwien.qse.sepm.dao.repo;

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
import at.ac.tuwien.qse.sepm.entities.Photo;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.*;

public abstract class PhotoCacheTest extends PhotoProviderTest {

    protected abstract PhotoCache getObject();

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getFile1() {
                return Paths.get("some/file.jpg");
            }

            @Override public Path getFile2() {
                return Paths.get("other/photo.jpg");
            }
        };
    }

    @Override protected void add(PhotoProvider object, Photo photo) throws DAOException {
        ((PhotoCache)object).put(photo);
    }

    @Test
    public void put_minimalData_persists() throws DAOException {
        PhotoCache object = getObject();
        Photo photo = new Photo(getContext().getFile1(), getContext().getMinimalData());

        object.put(photo);
        assertEquals(photo, object.read(photo.getFile()));
    }

    @Test
    public void put_maximalData_persists() throws DAOException {
        PhotoCache object = getObject();
        Photo photo = new Photo(getContext().getFile1(), getContext().getMaximalData());

        object.put(photo);
        assertEquals(photo, object.read(photo.getFile()));
    }

    @Test(expected = PhotoNotFoundException.class)
    public void remove_nonExisting_throws() throws DAOException {
        PhotoCache object = getObject();
        Path file = getContext().getFile1();

        object.remove(file);
    }

    @Test
    public void remove_existing_persists() throws DAOException {
        PhotoCache object = getObject();
        Photo photo = getContext().getPhoto1();
        object.put(photo);

        object.remove(photo.getFile());
        assertTrue(object.index().isEmpty());
    }
}
