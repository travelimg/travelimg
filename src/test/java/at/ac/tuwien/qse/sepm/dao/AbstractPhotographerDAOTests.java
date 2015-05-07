package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractPhotographerDAOTests {

    private PhotographerDAO photographerDAO;

    public void setPhotographerDAO(PhotographerDAO photographerDAO ) {
        this.photographerDAO = photographerDAO;
    }

    @Test(expected = ValidationException.class)
    public void createWithNullShouldThrow() throws ValidationException, DAOException {
        photographerDAO.create(null);
    }

    @Test
    public void createWithValidParameterShouldPersist() throws ValidationException, DAOException {
        Photographer p = photographerDAO.create(new Photographer(null, "Enri"));
        assertFalse(p.getId() == null);
    }

    @Test(expected = DAOException.class)
    public void readWithNonExistingIdShouldThrow() throws DAOException {
        photographerDAO.read(new Photographer(1337, null));
    }

    @Test
    public void readWithValidIdShouldReturnPhotographer() throws DAOException {
        Photographer p = photographerDAO.read(new Photographer(1,null));
        assertTrue(p.getName().equals("Alex Kinara"));
    }

}
