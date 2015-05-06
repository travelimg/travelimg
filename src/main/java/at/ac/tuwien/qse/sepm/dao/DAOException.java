package at.ac.tuwien.qse.sepm.dao;


import org.apache.commons.imaging.ImageReadException;

import java.io.IOException;
import java.sql.SQLException;

public class DAOException extends Exception {
    public DAOException() {
        super();
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, SQLException e) {
        super(message, e);
    }

    public DAOException(String message, IOException e) {
        super(message, e);
    }

    public DAOException(String message, ImageReadException e) {
        super(message, e);
    }
}
