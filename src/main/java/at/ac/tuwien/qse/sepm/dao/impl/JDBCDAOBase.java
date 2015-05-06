package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

public class JDBCDAOBase {

    private static final Logger logger = LogManager.getLogger();

    private Connection con = null;

    protected Connection getConnection() throws DAOException {
        if(con == null)
            con = DBConnection.getConnection();

        return con;
    }

}
