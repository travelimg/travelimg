package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

@UsingTable("Photographer")
public class JDBCPhotographerDAOTest extends AbstractJDBCDAOTest {

    @Autowired PhotographerDAO photographerDAO;

    @Test
    public void testWithEmptyDB() throws DAOException {
        assertThat(countRows(), is(0));
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        assertThat(photographerDAO.readAll().size(), is(countRows()));
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
        assertThat(countRows(), is(nrOfRows));
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void updateWithInvalidNameShouldThrow() throws DAOException, ValidationException {
        photographerDAO.update(new Photographer(1,""));
    }

    @Test
    @WithData
    public void updateWithValidNameShouldUpdate() throws DAOException, ValidationException {
        photographerDAO.update(new Photographer(1, "Enri"));
        assertThat(photographerDAO.getById(1).getName(), equalTo("Enri"));
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
        assertThat(countRowsWhere("name = 'Test Photographer'"), is(1));
    }

    @Test
    @WithData
    public void readAllShouldReturnPhotographers() throws DAOException {
        List<Photographer> photographers = photographerDAO.readAll();
        assertThat(photographers.size(), is(countRows()));
    }
}
