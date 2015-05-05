package at.ac.tuwien.qse.sepm.dao;


import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;

import java.io.IOException;
import java.sql.SQLException;

public class DAOException extends Exception {
    public DAOException() {
    }

    public DAOException(String s) {
        super(s);
    }

    public DAOException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DAOException(Throwable throwable) {
        super(throwable);
    }

    public DAOException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
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
