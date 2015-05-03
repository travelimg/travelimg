package at.ac.tuwien.qse.sepm.dao;


import at.ac.tuwien.qse.sepm.dao.impl.JDBCPhotoDAO;
import org.junit.After;
import org.junit.Before;

public class JDBCPhotoDAOTests extends AbstractPhotoDAOTests {

    @Before
    public void setUp(){
        setPhotoDAO(new JDBCPhotoDAO());
    }

    @After
    public void tearDown() {

    }

}
