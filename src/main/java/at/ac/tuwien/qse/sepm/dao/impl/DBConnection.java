package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @deprecated
 */
public class DBConnection {
    private static Connection con = null;
    private static final Logger logger = LogManager.getLogger();

    private DBConnection() throws DAOException {
        logger.warn("Deprecated. Use jdbcTemplate from JDBCDAOBase");
        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection("jdbc:h2:file:~/travelimg.db", "sa", "");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load H2 driver", e);
            throw new DAOException("Failed to load H2 driver", e);
        } catch(SQLException e) {
            logger.error("Failed to open database connection", e);
            throw new DAOException("Failed to open database connection", e);
        }
    }

    /**
     * @return the only possible connection to the database in this application.
     * @throws DAOException if could not establish this connection.
     */
    public static Connection getConnection() throws DAOException {
        if(con == null){
            new DBConnection();
            logger.info("Connection to the database established.");
        }

        return con;
    }

    /**
     * Closes the connection to the database.
     * @throws DAOException if it could not close this connection.
     */
    public static void closeConnection() throws DAOException {
        if(con != null){
            try {
                con.close();
                logger.info("Connection to the database closed successfully.");
            } catch (SQLException e) {
                logger.warn("Failed to close database connection", e);
                throw new DAOException("Failed to close database connection", e);
            }
        }
    }
}
