package at.ac.tuwien.qse.sepm.dao.impl;

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
}
