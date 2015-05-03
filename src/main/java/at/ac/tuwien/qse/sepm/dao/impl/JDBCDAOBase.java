package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;

import java.sql.Connection;

public class JDBCDAOBase {

    private Connection con = null;

    protected Connection getConnection() throws DAOException {
        if(con == null)
            con = DBConnection.getConnection();

        return con;
    }

}
