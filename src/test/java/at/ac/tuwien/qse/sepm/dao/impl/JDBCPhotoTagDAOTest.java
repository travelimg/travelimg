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
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UsingTable("PhotoTag")
public class JDBCPhotoTagDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    PhotoTagDAO photoTagDAO;

    @Test
    @WithData
    public void testWithData() throws DAOException {
        // fails because test_data_insert.sql is incomplete
        assertEquals(0, countRows());

    }


    @Test
    @WithData
    public void testcreateNewPhotoTagWithValidParamShouldPersist() throws ValidationException, DAOException {
        Photo p = new Photo();
        p.setId(1);

        Tag t = new Tag(1, "Strand");
        photoTagDAO.createPhotoTag(p, t);
        assertEquals(1, countRows());

    }

   /* @Test
    @WithData
    public void testremoveTagFromPhotoShouldPersist() throws ValidationException, DAOException {
        Photo p = new Photo();
        p.setId(1);

        Tag t = new Tag(1, "Strand");

        photoTagDAO.removeTagFromPhoto(p, t);
        assertEquals(0, countRows());

    }*/

    @Test
    @WithData
    public void testdeleteAllEntriesOfTagShouldPersist() throws ValidationException, DAOException {

        Tag t = new Tag(1,"Sonne");

        photoTagDAO.deleteAllEntriesOfSpecificTag(t);
        assertTrue(true);

    }

    @Test
    @WithData
    public void testdeleteAllEntriesOfPhotoShouldPersist() throws ValidationException, DAOException {

        Photo p = new Photo();
        p.setId(1);
        p.getData().setLatitude(12);
        p.getData().setLongitude(12);

        photoTagDAO.deleteAllEntriesOfSpecificPhoto(p);
        assertTrue(true);

    }

    @Test
    @WithData
    public void removeTagFromPhotoShouldPersist() throws ValidationException, DAOException {
        Photo p = new Photo();
        p.setId(1);

        Tag t = new Tag(1, "Strand");

        photoTagDAO.removeTagFromPhoto(p, t);
        assertEquals(0,countRows());

    }
    public void deleteAllEntriesOfTagShouldPersist() throws ValidationException, DAOException {

        Tag t = new Tag(1,"Sonne");

        photoTagDAO.deleteAllEntriesOfSpecificTag(t);
        assertTrue(true);

    }
    @Test
    @WithData
    public void testremoveTagFromPhotoShouldPersist() throws ValidationException, DAOException {
        Photo p = new Photo();
        p.setId(1);

        Tag t = new Tag(1, "Strand");

        photoTagDAO.removeTagFromPhoto(p, t);
        assertEquals(0,countRows());

    }

}