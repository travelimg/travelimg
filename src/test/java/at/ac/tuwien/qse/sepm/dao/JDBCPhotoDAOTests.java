package at.ac.tuwien.qse.sepm.dao;


import at.ac.tuwien.qse.sepm.dao.impl.DBConnection;
import at.ac.tuwien.qse.sepm.dao.impl.JDBCPhotoDAO;
import org.junit.After;
import org.junit.Before;

public class JDBCPhotoDAOTests extends AbstractPhotoDAOTests {


    @Before
    public void setUp() throws Exception {
        //erstellt H2 Verbindung
        //ggf. Datenbank erstellen (sofern In-Memory und nicht Server-Mode)
        //Testdaten einspielen
        //erstelle H2JockeyDAO
        PhotoDAO re = new JDBCPhotoDAO("/tmp");
        //setzte JockeyDAO
        setPhotoDAO(re);
        DBConnection.getConnection().setAutoCommit(false);
    }

    @After
    public void tearDown() throws Exception {
        DBConnection.getConnection().rollback();
    }

}
