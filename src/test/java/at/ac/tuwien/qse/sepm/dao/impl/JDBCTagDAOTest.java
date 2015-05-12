package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Tag;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

@UsingTable("Tag")
public class JDBCTagDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    TagDAO tagDAO;

    @Test
    public void testWithEmptyDB() throws DAOException {
        assertEquals(0, countRows());
        assertEquals(0, countRowsWhere("name = 'Test'"));
        tagDAO.create(new Tag(1, "Test"));
        assertEquals(1, countRows());
        assertEquals(1, countRowsWhere("name = 'Test'"));
        assertEquals(0, countRowsWhere("name = 'Unknown'"));
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        assertEquals(2, countRows());
        tagDAO.create(new Tag(1, "Test"));
        assertEquals(3, countRows());
    }
}
