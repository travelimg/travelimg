package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;

import java.io.IOException;

/**
 * Exception for service errors.
 */
public class ServiceException extends Exception {
    public ServiceException() {
    }

    public ServiceException(String s) {
        super(s);
    }

    public ServiceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServiceException(Throwable throwable) {
        super(throwable);
    }

    public ServiceException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
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
