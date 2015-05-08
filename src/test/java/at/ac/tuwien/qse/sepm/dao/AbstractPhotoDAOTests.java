package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public void testReadPhotosByDateShouldReturnPhotos() throws DAOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        List<Photo> photos = photoDAO.readPhotosByDate(sdf.parse("03-2015"));
        assertTrue(photos.size() > 0);
    }
}
