package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public abstract class PhotoCacheTest extends PhotoProviderTest {

    protected abstract PhotoCache getObject();

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getFile1() {
                return Paths.get("some/file.jpg");
            }

            @Override public Path getFile2() {
                return Paths.get("other/photo.jpg");
            }
        };
    }

    @Override protected void add(PhotoProvider object, Photo photo) throws DAOException {
        ((PhotoCache)object).put(photo);
    }

    @Test
    public void put_valid_persists() throws DAOException {
        PhotoCache object = getObject();
        Photo photo = getContext().getPhoto1();

        object.put(photo);
        assertEquals(photo, object.read(photo.getFile()));
    }

    @Test(expected = PhotoNotFoundException.class)
    public void remove_nonExisting_throws() throws DAOException {
        PhotoCache object = getObject();
        Path file = getContext().getFile1();

        object.remove(file);
    }

    @Test
    public void remove_existing_persists() throws DAOException {
        PhotoCache object = getObject();
        Photo photo = getContext().getPhoto1();
        object.put(photo);

        object.remove(photo.getFile());
        assertTrue(object.index().isEmpty());
    }
}
