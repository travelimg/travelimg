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
        Slideshow s1 = new Slideshow(1,"Testname",5.0);

        slideshowDAO.update(s1);
    }

    @Test(expected = ValidationException.class)
    public void update_with_null_should() throws ValidationException, DAOException {
        Slideshow s1 = new Slideshow(1,null,5.0);

        slideshowDAO.update(s1);

    }
    @Test
    public void delete_should_persist() throws ValidationException,DAOException {
        Slideshow s1 = new Slideshow(1,"test",5.0);
        slideshowDAO.delete(s1);
    }
    @Test
    public void readAll_should_return_correct_count() throws ValidationException,DAOException {
        assertThat(slideshowDAO.readAll().size(), is(countRows()));

    }
}
