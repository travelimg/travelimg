package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;

import java.io.IOException;

public class ServiceException extends Exception {
    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, IOException e) {
        super(message, e);
    }

    public ServiceException(String message, DAOException e) {
        super(message, e);
    }

    public ServiceException(String message, ImageWriteException e) {
        super(message, e);
    }

    public ServiceException(String message, ImageReadException e) {
        super(message, e);
    }
}
