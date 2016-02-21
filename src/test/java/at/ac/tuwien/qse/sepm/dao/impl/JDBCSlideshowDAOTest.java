package at.ac.tuwien.qse.sepm.dao.impl;

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

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import javax.xml.bind.ValidationEvent;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

@UsingTable("slideshow")
public class JDBCSlideshowDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    SlideshowDAO slideshowDAO;

    @Test
    public void test_with_EmptyDB() throws DAOException {
        assertThat(countRows(),is(0));
    }

    @Test
    public void test_with_Data() throws DAOException {
        assertThat(slideshowDAO.readAll().size(),is(countRows()));
    }

    @Test(expected = ValidationException.class)
    public void create_with_null_should_throws() throws ValidationException, DAOException {
        slideshowDAO.create(null);
    }

    @Test
    public void update_should_persist() throws ValidationException,DAOException {
        Slideshow s1 = new Slideshow(1,"Testname",5.0);

        slideshowDAO.update(s1);
    }

    @Test(expected = ValidationException.class)
    public void update_with_null_should() throws ValidationException, DAOException {
        Slideshow s1 = new Slideshow(1,null,5.0);

        slideshowDAO.update(s1);

    }
    @Test
    public void delete_should_persist() throws ValidationException,DAOException {
        Slideshow s1 = new Slideshow(1,"test",5.0);
        slideshowDAO.delete(s1);
    }
    @Test
    public void readAll_should_return_correct_count() throws ValidationException,DAOException {
        assertThat(slideshowDAO.readAll().size(), is(countRows()));

    }
}
