package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;

import static org.mockito.Mockito.mock;

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
    public void testDelete() throws DAOException, ValidationException {
        Photo mockPhoto = mock(Photo.class);
        photoDAO.create(mockPhoto);
    }
}
