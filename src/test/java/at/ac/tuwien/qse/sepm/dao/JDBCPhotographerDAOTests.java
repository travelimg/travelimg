package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.dao.impl.JDBCPhotographerDAO;
import org.junit.After;
import org.junit.Before;

public class JDBCPhotographerDAOTests extends AbstractPhotographerDAOTests {
    @Before
    public void setUp(){
        setPhotographerDAO(new JDBCPhotographerDAO());
    }

    @After
    public void tearDown() {

    }
}
