package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.TagDAO;
import at.ac.tuwien.qse.sepm.entities.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class JDBCTagDAO extends JDBCDAOBase implements TagDAO {

    private static final Logger logger = LogManager.getLogger();

    public Tag create(Tag t) throws DAOException {
        return null;
    }

    public Tag read(Tag t) throws DAOException {
        return null;
    }

    public void update(Tag t) throws DAOException {

    }

    public void delete(Tag t) throws DAOException {

    }

    public List<Tag> readAll() throws DAOException {
        return null;
    }
}
