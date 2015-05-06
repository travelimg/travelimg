package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

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

}
