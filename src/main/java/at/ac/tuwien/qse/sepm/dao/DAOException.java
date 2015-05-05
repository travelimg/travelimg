package at.ac.tuwien.qse.sepm.dao;


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
}
