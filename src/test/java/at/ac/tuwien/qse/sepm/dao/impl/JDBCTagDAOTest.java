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
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UsingTable("Tag")
public class JDBCTagDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    TagDAO tagDAO;

    @Test
    public void testWithEmptyDB() throws DAOException {
        assertEquals(0, countRows());
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        assertEquals(tagDAO.readAll().size(), countRows());
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void createWithNullShouldThrow() throws ValidationException, DAOException {
        tagDAO.create(null);
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void createWithNameWithWhiteSpacesShouldThrow() throws ValidationException, DAOException {
        tagDAO.create(new Tag(null, " "));
    }

    @Test
    @WithData
    public void createWithValidParameterShouldPersist() throws ValidationException, DAOException {
        int nrOfRows = countRows();
        Tag t = tagDAO.create(new Tag(null, "Strand"));
        assertEquals(nrOfRows + 1, countRows());
    }

    @Test(expected = DAOException.class)
    @WithData
    public void readWithNonExistingIdShouldThrow() throws DAOException {
        tagDAO.read(new Tag(-1, null));
    }

    @Test
    @WithData
    public void readWithValidIdShouldReturnTag() throws DAOException {
        Tag t = tagDAO.read(new Tag(1, null));
        assertTrue(t.getName().equals("Person"));
        assertEquals(1, countRowsWhere("name = 'Person'"));
    }

    @WithData
    public void deleteWithNonExistingIdShouldNotChangeAnything() throws DAOException {
        int nrOfRows = countRows();
        tagDAO.delete(new Tag(-1, null));
        assertEquals(nrOfRows, countRows());
    }

    @Test
    @WithData
    public void deleteShouldRemoveOneRow() throws DAOException {
        int nrOfRows = countRows();
        tagDAO.delete(new Tag(1, null));
        assertEquals(countRows(), nrOfRows - 1);
    }

    @Test
    @WithData
    public void readAllShouldReturnTags() throws DAOException {
        List<Tag> tags = tagDAO.readAll();
        assertEquals(countRows(), tags.size());
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void testCreateDuplicateThrows() throws DAOException, ValidationException {
        tagDAO.create(new Tag(1, "Essen"));
    }
}
