package at.ac.tuwien.qse.sepm.dao;


import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;

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

    public DAOException(String message, MetadataException e) {
        super(message, e);
    }

    public DAOException(String message, IOException e) {
        super(message, e);
    }

    public DAOException(String message, ImageProcessingException e) {
        super(message, e);
    }

}
