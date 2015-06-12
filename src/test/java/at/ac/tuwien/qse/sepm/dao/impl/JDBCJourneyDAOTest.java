package at.ac.tuwien.qse.sepm.dao.impl;

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


}
