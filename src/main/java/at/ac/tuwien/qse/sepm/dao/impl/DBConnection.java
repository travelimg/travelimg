package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection con = null;
    private static final Logger logger = LogManager.getLogger(DBConnection.class);

    private DBConnection() throws DAOException {
        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/mydb", "sa", "");
        } catch (ClassNotFoundException e) {
            logger.debug(e.getMessage());
            throw new DAOException(e.getMessage());
        } catch (SQLException e) {
            logger.fatal(e.getMessage());
            throw new DAOException("Couldn't connect to database. Please try again.");
        }
    }

    /**
     * @return the only possible connection to the database in this application.
     * @throws DAOException if could not establish this connection.
     */
    public static Connection getConnection() throws DAOException {
        if(con==null){
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
        if(con!=null){
            try {
                con.close();
                logger.info("Connection to the database closed successfully.");
            } catch (SQLException e) {
                logger.warn(e.getMessage());
                throw new DAOException(e.getMessage());
            }
        }
    }


}
