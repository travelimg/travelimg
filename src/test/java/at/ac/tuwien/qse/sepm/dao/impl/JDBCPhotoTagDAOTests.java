package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UsingTable("PhotoTag")
public class JDBCPhotoTagDAOTests extends AbstractJDBCDAOTests {

    @Autowired
    PhotoTagDAO photoTagDAO;

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