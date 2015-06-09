package at.ac.tuwien.qse.sepm.dao.repo;

public class FormatException extends PersistenceException {

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
