package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;

import static junit.framework.Assert.assertEquals;

@UsingTable("Photo")
public class JDBCPhotoDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    PhotoDAO photoDAO;

    private final String dataDir = Paths.get(System.getProperty("java.io.tmpdir"), "travelimg").toString();

    @Test
    public void testWithEmptyDB() throws DAOException {
        assertEquals(0, countRows());
        assertTrue(false);
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        // fails because test_data_insert.sql is incomplete
        assertEquals(1, countRows());
    }
}
