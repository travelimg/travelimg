package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.AbstractJDBCExifDAOTest;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import org.junit.After;
import org.junit.Before;

/**
 * Created by christoph on 08.05.15.
 */
public class JDBCExifDAOTest extends AbstractJDBCExifDAOTest {

    @Before
    public void setUp() throws Exception {
        //erstellt H2 Verbindung
        //ggf. Datenbank erstellen (sofern In-Memory und nicht Server-Mode)
        //Testdaten einspielen
        //erstelle H2JockeyDAO
        ExifDAO re = new JDBCExifDAO();
        //setzte JockeyDAO
        setExifDAO(re);
        DBConnection.getConnection().setAutoCommit(false);
    }

    @After
    public void tearDown() throws Exception {
        DBConnection.getConnection().rollback();
    }



}