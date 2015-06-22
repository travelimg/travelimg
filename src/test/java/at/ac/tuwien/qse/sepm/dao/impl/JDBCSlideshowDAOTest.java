package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.AbstractJDBCDAOTest;
import at.ac.tuwien.qse.sepm.dao.SlideshowDAO;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Branko on 22.06.15.
 */
public class JDBCSlideshowDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    SlideshowDAO slideshowDAO;
}
