package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UsingTable("Photographer")
public class JDBCPhotographerDAOTest extends AbstractJDBCDAOTest {

    @Autowired PhotographerDAO photographerDAO;

    @Test
    public void testWithEmptyDB() throws DAOException {
        assertEquals(0, countRows());
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        assertEquals(photographerDAO.readAll().size(), countRows());
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void createWithNullShouldThrow() throws ValidationException, DAOException {
        photographerDAO.create(null);
    }

    @Test
    @WithData
    public void createWithValidParameterShouldPersist() throws ValidationException, DAOException {
        int nrOfRows = countRows();
        Photographer p = photographerDAO.create(new Photographer(null, "Enri"));
        assertEquals(nrOfRows+1, countRows());
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void updateWithInvalidNameShouldThrow() throws DAOException, ValidationException {
        photographerDAO.update(new Photographer(1,""));
    }

    @Test
    @WithData
    public void updateWithValidNameShouldUpdate() throws DAOException, ValidationException {
        photographerDAO.update(new Photographer(1,"Enri"));
        assertEquals("Enri",photographerDAO.getById(1).getName());
    }

    @Test(expected = DAOException.class)
    @WithData
    public void readWithNonExistingIdShouldThrow() throws DAOException {
        photographerDAO.getById(-1);
    }

    @Test
    @WithData
    public void readWithValidIdShouldReturnPhotographer() throws DAOException {
        Photographer p = photographerDAO.getById(1);
        assertTrue(p.getName().equals("Test Photographer"));
        assertEquals(1, countRowsWhere("name = 'Test Photographer'"));
    }

    @Test
    @WithData
    public void readAllShouldReturnPhotographers() throws DAOException {
        List<Photographer> photographers = photographerDAO.readAll();
        assertEquals(countRows(), photographers.size());
    }
}
