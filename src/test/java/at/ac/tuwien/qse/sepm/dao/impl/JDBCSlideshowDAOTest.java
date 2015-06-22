package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import javax.xml.bind.ValidationEvent;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

@UsingTable("slideshow")
public class JDBCSlideshowDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    SlideshowDAO slideshowDAO;

    @Test
    public void test_with_EmptyDB() throws DAOException {
        assertThat(countRows(),is(0));
    }

    @Test
    public void test_with_Data() throws DAOException {
        assertThat(slideshowDAO.readAll().size(),is(countRows()));
    }

    @Test(expected = ValidationException.class)
    public void create_with_null_should_throws() throws ValidationException, DAOException {
        slideshowDAO.create(null);
    }

    @Test
    public void update_should_persist() throws ValidationException,DAOException {
        Slideshow s1 = new Slideshow();
        s1.setId(1);
        s1.setName("Testname");
        s1.setDurationBetweenPhotos(5.0);
        slideshowDAO.update(s1);
    }

    @Test(expected = ValidationException.class)
    public void update_with_null_should() throws ValidationException, DAOException {
        Slideshow s1 = new Slideshow();
        s1.setName(null);
        slideshowDAO.update(s1);

    }
    @Test
    public void delete_should_persist() throws ValidationException,DAOException {
        Slideshow s1 = new Slideshow();
        s1.setId(1);
        slideshowDAO.delete(s1);
    }
    @Test
    public void readAll_should_return_correct_count() throws ValidationException,DAOException {
        assertThat(slideshowDAO.readAll().size(), is(countRows()));

    }
    @Test(expected = AssertionError.class)
    public void get_slideshow_by_id_should_throw() throws ValidationException,DAOException {
        Slideshow s2 = new Slideshow();
        s2.setName("Test");
        s2.setId(1);
        s2.setDurationBetweenPhotos(5.0);
        slideshowDAO.create(s2);

        assertThat(slideshowDAO.getById(1),is(s2));

    }
}
