package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by christoph on 08.05.15.
 */
public class AbstractJDBCExifDAOTest extends TestCase{

    private ExifDAO exifDAO;

    public void setExifDAO(ExifDAO exifDAO) {
        this.exifDAO = exifDAO;
    }

    @Test
    public void testDelete() throws Exception {
        assertTrue(false);

        /*Exif e = mock(Exif.class);
        when(e.getId()).thenReturn(2);
        exifDAO.delete(e);
        assertFalse(exifDAO.read(e) != null);
        assertNull(exifDAO.read(e));*/
    }
}
