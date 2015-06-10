package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@UsingTable("Place")
public class JDBCPlaceDAOTest extends AbstractJDBCDAOTest {

    @Autowired PlaceDAO placeDAO;

    @Test
    public void testWithEmptyDB() throws DAOException {
        assertThat(countRows(), is(0));
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        assertThat(placeDAO.readAll().size(), is(countRows()));
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void createWithNullShouldThrow() throws ValidationException, DAOException {
        placeDAO.create(null);
    }

    @Test
    @WithData
    public void createWithValidParameterShouldPersist() throws ValidationException, DAOException {
        int nrOfRows = countRows();
        Place p = placeDAO.create(new Place(null, "Vienna","Austria",0.0,0.0,new Journey(1,"test",
                LocalDateTime.of(2010, 3, 6, 0, 0, 0),LocalDateTime.of(2015, 3, 6, 0, 0, 0))));
        assertThat(countRows(), is(nrOfRows + 1));
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void updateWithIdNullShouldThrow() throws DAOException, ValidationException {
        placeDAO.update(new Place(null, "Vienna","Austria",0.0,0.0,new Journey(1,"test",
                LocalDateTime.of(2010, 3, 6, 0, 0, 0),LocalDateTime.of(2015, 3, 6, 0, 0, 0))));
    }

    @Test
    @WithData
    public void updateWithValidNameShouldUpdate() throws DAOException, ValidationException {
        placeDAO.update(new Place(1, "Vienna","Austria",0.0,0.0,new Journey(1,"test",
                LocalDateTime.of(2010, 3, 6, 0, 0, 0),LocalDateTime.of(2015, 3, 6, 0, 0, 0))));
        assertThat(placeDAO.getById(1).getCity(), equalTo("Vienna"));
    }

    @Test(expected = DAOException.class)
    @WithData
    public void readWithNonExistingIdShouldThrow() throws DAOException, ValidationException {
        placeDAO.getById(0);
    }

    @Test
    @WithData
    public void readWithValidIdShouldReturnPlace() throws DAOException, ValidationException {
        assertThat(placeDAO.getById(1).getCountry(), equalTo("Unknown place"));
        assertThat(countRowsWhere("country = 'Unknown place'"), is(1));
    }

    @Test
    @WithData
    public void readByJourneyWithNonExistingIdReturnNoPlaces() throws DAOException, ValidationException {
        assertThat(countRowsWhere("journey_id = 0"), is(placeDAO.readByJourney(new Journey(0, null, null, null)).size()));
    }

    @Test
    @WithData
    public void readWithValidJourneyIdShouldReturnPlaces() throws DAOException, ValidationException {
        assertThat(countRowsWhere("journey_id = 1"),
                is(placeDAO.readByJourney(new Journey(1, null, null, null)).size()));
    }
}
