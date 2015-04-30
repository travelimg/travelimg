package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Service {

    private static final Logger logger = LogManager.getLogger(Service.class);
    //just for testing the connection to the db
    public Service(){
        try {
            DBConnection.getConnection();
        } catch (DAOException e) {
           logger.fatal(e.getMessage());
        }
    }
}
