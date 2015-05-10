package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.AbstractJDBCPhotoTagDAOTest;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.dao.impl.JDBCPhotoTagDAO;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

/**
 * Created by christoph on 08.05.15.
 */
public class JDBCPhotoTagDAOTest extends AbstractJDBCPhotoTagDAOTest {

    @Before
    public void setUp() throws Exception {

        PhotoTagDAO pt = new JDBCPhotoTagDAO();

        setPhotoTagDAO(pt);
        DBConnection.getConnection().setAutoCommit(false);
    }

    @After
    public void tearDown() throws Exception {
        DBConnection.getConnection().rollback();
    }
}