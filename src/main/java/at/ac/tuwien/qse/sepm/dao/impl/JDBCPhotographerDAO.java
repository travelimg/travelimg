package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotographerDAO;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.PhotographerValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCPhotographerDAO extends JDBCDAOBase implements PhotographerDAO {

    private static final Logger logger = LogManager.getLogger();

    private static final String readStatement = "SELECT ID,name FROM Photographer WHERE ID=?;";
    private static final String readAllStatement = "SELECT ID,name FROM Photographer;";

    private SimpleJdbcInsert insertPhotographer;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertPhotographer = new SimpleJdbcInsert(dataSource)
                .withTableName("Photographer")
                .usingGeneratedKeyColumns("id");
    }

    public Photographer create(Photographer p) throws DAOException, ValidationException {
        logger.debug("Creating photographer {}", p);
        PhotographerValidator.validate(p);

        try {
            Map<String, Object> parameters = new HashMap<String, Object>(1);
            parameters.put("name", p.getName());
            Number newId = insertPhotographer.executeAndReturnKey(parameters);
            p.setId((int) newId.longValue());
            return p;
        } catch (DataAccessException e) {
            throw new DAOException("Failed to create photographer", e);
        }
    }

    public Photographer read(Photographer p) throws DAOException {
        try {
            return this.jdbcTemplate.queryForObject(
                    readStatement,
                    new Object[]{p.getId()},
                    new RowMapper<Photographer>() {
                        @Override
                        public Photographer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Photographer p = new Photographer();
                            p.setId(rs.getInt(1));
                            p.setName(rs.getString(2));
                            return p;
                        }
                    });
        }
        catch(DataAccessException e){
            throw new DAOException("Failed to read a photographer", e);
        }
    }

    public List<Photographer> readAll() throws DAOException {
        try {
            return jdbcTemplate.query(readAllStatement, new RowMapper<Photographer>() {
                @Override
                public Photographer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Photographer(rs.getInt(1), rs.getString(2));
                }
            });
        }
        catch(DataAccessException e){
            throw new DAOException("Failed to read all photographers", e);
        }
    }

}
