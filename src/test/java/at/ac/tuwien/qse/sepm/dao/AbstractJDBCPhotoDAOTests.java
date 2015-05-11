package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by christoph on 09.05.15.
 */
public class AbstractJDBCPhotoDAOTests {

    private PhotoDAO photoDAO;

    public void  setPhotoDAO(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }

    @Test
    public void deleteTagFromPhoto() throws DAOException, ValidationException {

    }
}
