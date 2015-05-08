package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

public class JDBCDAOBase {

    protected static final Logger logger = LogManager.getLogger();

    protected JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private Connection con = null;

    /**
     * @deprecated
     */
    protected Connection getConnection() throws DAOException {
        logger.warn("Deprecated use of getConnection(). Use jdbcTemplate.");
        if(con == null)
            con = DBConnection.getConnection();

        return con;
    }

}
