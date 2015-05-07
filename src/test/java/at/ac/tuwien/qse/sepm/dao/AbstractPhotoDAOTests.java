package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public abstract class AbstractPhotoDAOTests {

    private PhotoDAO photoDAO;

    public void setPhotoDAO(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }

    @Test(expected=ValidationException.class)
    public void testNullEntityShouldThrow() throws DAOException, ValidationException {
        photoDAO.create(null);
    }

    @Test
    public void testReadPhotosByYearAndMonthShouldReturnPhotos() throws DAOException {
        List<Photo> photos = photoDAO.readPhotosByYearAndMonth(2015,3);
        assertTrue(photos.size() > 0);
    }
}
