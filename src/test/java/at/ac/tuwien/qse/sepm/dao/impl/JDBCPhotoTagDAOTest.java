package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.AbstractJDBCPhotoTagDAOTest;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

/**
 * Created by christoph on 08.05.15.
 */
public class JDBCPhotoTagDAOTest extends AbstractJDBCPhotoTagDAOTest {

    @Before
    public void setUp() throws Exception {
        //erstellt H2 Verbindung
        //ggf. Datenbank erstellen (sofern In-Memory und nicht Server-Mode)
        //Testdaten einspielen
        //erstelle H2JockeyDAO
        PhotoTagDAO pt = new JDBCPhotoTagDAO();
        //setzte JockeyDAO
        setPhotoTagDAO(pt);
        DBConnection.getConnection().setAutoCommit(false);
    }

    @After
    public void tearDown() throws Exception {
        DBConnection.getConnection().rollback();
    }
}