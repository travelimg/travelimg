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
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCacheTest;
import at.ac.tuwien.qse.sepm.entities.Photo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JdbcPhotoCacheTest extends PhotoCacheTest {

    @Autowired
    private PhotoDAO photoDAO;

    @Autowired
    private PhotoCache photoCache;

    @Override protected PhotoCache getObject() {
        return photoCache;
    }

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getFile1() {
                return Paths.get("test/1.jpg");
            }

            @Override public Path getFile2() {
                return Paths.get("test/2.jpg");
            }
        };
    }

    @Test
    public void put_twice_persistsOnce() throws DAOException {
        PhotoCache object = getObject();
        Photo photo = getContext().getPhoto1();
        photo.setId(1);

        assertThat(object.readAll(), empty());
        assertThat(photoDAO.readAll(), empty());

        // insert photo with id 1
        object.put(photo);
        assertThat(object.readAll(), contains(photo));
        assertThat(photoDAO.readAll(), contains(photo));

        // now suppose a create event is encountered for the same photo and the photo has id null:
        // should only persist once
        photo.setId(null);
        object.put(photo);

        photo.setId(1);

        assertThat(object.readAll(), contains(photo));
        assertThat(object.readAll().size(), is(1));
        assertThat(photoDAO.readAll(), contains(photo));
        assertThat(photoDAO.readAll().size(), is(1));
    }
}