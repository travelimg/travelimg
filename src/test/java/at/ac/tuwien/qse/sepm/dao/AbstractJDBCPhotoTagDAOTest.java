package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by christoph on 08.05.15.
 */
public class AbstractJDBCPhotoTagDAOTest extends TestCase {

    private PhotoTagDAO photoTagDAO;

    public void  setPhotoTagDAO(PhotoTagDAO photoTagDAO) {
        this.photoTagDAO = photoTagDAO;
    }

    @Test
    public void deleteTagFromPhoto() throws DAOException, ValidationException{
        Tag t = mock(Tag.class);
        Photo p = mock(Photo.class);
        when(t.getId()).thenReturn(2);
        when(p.getId()).thenReturn(1);
        photoTagDAO.removeTagFromPhoto(t, p);
        assertNull(null);
    }
}
