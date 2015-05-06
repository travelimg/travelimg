package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;

public abstract class AbstractPhotoDAOTests {

    private PhotoDAO photoDAO;

    public void setPhotoDAO(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }

    @Test(expected=ValidationException.class)
    public void testNullEntityShouldThrow() throws DAOException, ValidationException {
        photoDAO.create(null);
    }
}
