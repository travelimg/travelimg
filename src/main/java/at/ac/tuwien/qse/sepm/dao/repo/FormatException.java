package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.dao.DAOException;

public class FormatException extends DAOException {

    public FormatException(Throwable cause) {
        super(cause);
    }

    public FormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormatException(String message) {
        super(message);
    }

    public FormatException() {
    }
}
