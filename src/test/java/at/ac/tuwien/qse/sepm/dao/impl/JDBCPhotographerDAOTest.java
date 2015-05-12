package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@UsingTable("Photographer")
public class JDBCPhotographerDAOTest extends AbstractJDBCDAOTest {

    @Autowired PhotographerDAO photographerDAO;

    @Test
    public void testWithEmptyDB() throws DAOException {
        assertEquals(0, countRows());
        assertTrue(false);
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        // fails because test_data_insert.sql is incomplete
        assertEquals(1, countRows());
    }

    @Test(expected = ValidationException.class)
    public void createWithNullShouldThrow() throws ValidationException, DAOException {
        photographerDAO.create(null);
    }

    @Test
    public void createWithValidParameterShouldPersist() throws ValidationException, DAOException {
        Photographer p = photographerDAO.create(new Photographer(null, "Enri"));
        assertFalse(p.getId() == null);
        assertEquals(1, countRows());
    }

    @Test(expected = DAOException.class)
    public void readWithNonExistingIdShouldThrow() throws DAOException {
        photographerDAO.read(new Photographer(1337, null));
    }

    @Test
    @WithData
    public void readWithValidIdShouldReturnPhotographer() throws DAOException {
        Photographer p = photographerDAO.read(new Photographer(1,null));
        assertTrue(p.getName().equals("Alex Kinara"));
        assertEquals(1, countRowsWhere("name = 'Alex Kinara'"));
    }

    @Test
    @WithData
    public void readAllShouldReturnPhotographers() throws DAOException {
        List<Photographer> photographers = photographerDAO.readAll();
        assertEquals(countRows(), photographers.size());
    }
}
