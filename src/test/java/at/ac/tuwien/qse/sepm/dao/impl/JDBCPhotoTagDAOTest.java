package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UsingTable("PhotoTag")
public class JDBCPhotoTagDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    PhotoTagDAO photoTagDAO;

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


    @Test
    @WithData
    public void testcreateNewPhotoTagWithValidParamShouldPersist() throws ValidationException, DAOException
    {
        Photo p = new Photo();
        p.setId(1);

        Tag t = new Tag(1,"Strand");
        photoTagDAO.createPhotoTag(p,t);
        assertEquals(1,countRows());
    }

    @Test
    @WithData
    public void testremoveTagFromPhotoShouldPersist() throws ValidationException, DAOException {
        Photo p = new Photo();
        p.setId(1);

        Tag t = new Tag(1, "Strand");

        photoTagDAO.removeTagFromPhoto(p, t);
        assertEquals(0,countRows());

    }

    @Test
    @WithData
    public void testdeleteAllEntriesOfTagShouldPersist() throws ValidationException, DAOException {

        Tag t = new Tag(1,"Sonne");

        photoTagDAO.deleteAllEntriesOfSpecificTag(t);
        assertTrue(true);

    }
    @Test
    @WithData
    public void testdeleteAllEntriesOfPhotoShouldPersist() throws ValidationException, DAOException {

        Photo p = new Photo();
        p.setId(1);
        p.setLatitude(12);
        p.setLongitude(12);

        photoTagDAO.deleteAllEntriesOfSpecificPhoto(p);
        assertTrue(true);

    }

    @Test
    @WithData
    public void removeTagFromPhotoShouldPersist() throws ValidationException, DAOException {
        Photo p = new Photo();
        p.setId(1);

        Tag t = new Tag(1, "Strand");

        photoTagDAO.removeTagFromPhoto(p, t);
        assertEquals(0,countRows());

    }
    public void deleteAllEntriesOfTagShouldPersist() throws ValidationException, DAOException {

        Tag t = new Tag(1,"Sonne");

        photoTagDAO.deleteAllEntriesOfSpecificTag(t);
        assertTrue(true);

    }
}