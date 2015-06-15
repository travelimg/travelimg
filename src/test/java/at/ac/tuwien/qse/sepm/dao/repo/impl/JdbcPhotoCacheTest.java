package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCacheTest;
import at.ac.tuwien.qse.sepm.entities.Photo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JdbcPhotoCacheTest extends PhotoCacheTest {

    @Autowired
    private PhotoDAO photoDAO;

    @Autowired
    private PhotoCache photoCache;

    @Override protected PhotoCache getObject() {
        return photoCache;
    }

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getFile1() {
                return Paths.get("test/1.jpg");
            }

            @Override public Path getFile2() {
                return Paths.get("test/2.jpg");
            }
        };
    }

    @Test
    public void put_twice_persistsOnce() throws DAOException {
        PhotoCache object = getObject();
        Photo photo = getContext().getPhoto1();
        photo.setId(1);

        assertThat(object.readAll(), empty());
        assertThat(photoDAO.readAll(), empty());

        // insert photo with id 1
        object.put(photo);
        assertThat(object.readAll(), contains(photo));
        assertThat(photoDAO.readAll(), contains(photo));

        // now suppose a create event is encountered for the same photo and the photo has id null:
        // should only persist once
        photo.setId(null);
        object.put(photo);

        photo.setId(1);

        assertThat(object.readAll(), contains(photo));
        assertThat(object.readAll().size(), is(1));
        assertThat(photoDAO.readAll(), contains(photo));
        assertThat(photoDAO.readAll().size(), is(1));
    }
}