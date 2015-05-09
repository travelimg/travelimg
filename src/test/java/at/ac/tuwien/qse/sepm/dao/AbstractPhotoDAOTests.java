import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;



<<<<<<< HEAD
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
=======
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
>>>>>>> spring-ioc

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
<<<<<<< HEAD

    public void testDelete() throws DAOException, ValidationException {
        Photo e = mock(Photo.class);
        Exif ex = mock(Exif.class);
        Photographer p = mock(Photographer.class);
        when(e.getId()).thenReturn(2);
        when(ex.getId()).thenReturn(2);
        when(p.getId()).thenReturn(2);
        when(e.getPhotographer()).thenReturn(p);
        when(e.getPath()).thenReturn("test1");
        when(e.getRating()).thenReturn(0);
        photoDAO.delete(e);


    }
   // public void testReadPhotosByYearAndMonthShouldReturnPhotos() throws DAOException {
   //     List<Photo> photos = photoDAO.readPhotosByYearAndMonth(2015,3);
   //     assertTrue(photos.size() > 0);

   // }
=======
    public void testReadPhotosByYearAndMonthShouldReturnPhotos() throws DAOException {
        List<Photo> photos = photoDAO.readPhotosByYearAndMonth(2015,3);
        assertTrue(photos.size() > 0);
    }
>>>>>>> spring-ioc
}
