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
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

@UsingTable("Journey")
public class JDBCJourneyDAOTest  extends AbstractJDBCDAOTest {

    @Autowired JourneyDAO journeyDAO;

    @Test
    public void testWithEmptyDB() throws DAOException {
        assertEquals(0, countRows());
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        assertThat(journeyDAO.readAll().size(), is(countRows()));
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void createWithNullShouldThrow() throws ValidationException, DAOException {
        journeyDAO.create(null);
    }

    @Test
    @WithData
    public void createWithValidParameterShouldPersist() throws ValidationException, DAOException {
        int nrOfRows = countRows();
        journeyDAO.create(new Journey(null,"Vienna", LocalDateTime.of(2010, 3, 6, 0, 0, 0),LocalDateTime.of(2015, 3, 6, 0, 0, 0)));
        assertThat(countRows(), is(nrOfRows + 1));
    }

    @Test
    @WithData
    public void deleteWithValidParameterShouldRemoveJourney() throws ValidationException, DAOException {
        int nrOfRows = countRows();
        journeyDAO.delete(new Journey(1,null, null,null));
        assertThat(countRows(), is(nrOfRows - 1));
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void updateWithIdNullShouldThrow() throws DAOException, ValidationException {
        journeyDAO.update(new Journey(null,"Vienna", LocalDateTime.of(2010, 3, 6, 0, 0, 0),LocalDateTime.of(2015, 3, 6, 0, 0, 0)));
    }

    @Test
    @WithData
    public void updateWithValidNameShouldUpdate() throws DAOException, ValidationException {
        journeyDAO.update(new Journey(1,"Vienna", LocalDateTime.of(2010, 3, 6, 0, 0, 0),LocalDateTime.of(2015, 3, 6, 0, 0, 0)));
        assertThat(journeyDAO.getByID(1).getName(), equalTo("Vienna"));
    }

    @Test
    @WithData
    public void readWithValidIdShouldReturnPlace() throws DAOException, ValidationException {
        assertThat(journeyDAO.getByID(1).getName(), equalTo("United States"));
        assertThat(countRowsWhere("name = 'United States'"), is(1));
    }

    @Test
    @WithData
    public void readByNameReturnsJourney() throws DAOException {
        assertThat(journeyDAO.getByName("Other").getId(), is(2));
    }


}
