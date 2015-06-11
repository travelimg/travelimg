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

    private static final String UPDATE_STATEMENT = "UPDATE Photographer SET name=? WHERE id=?";
    private static final String GET_BY_ID_STATEMENT = "SELECT ID,name FROM Photographer WHERE ID=?;";
    private static final String READ_ALL_STATEMENT = "SELECT ID,name FROM Photographer;";

    private SimpleJdbcInsert insertPhotographer;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertPhotographer = new SimpleJdbcInsert(dataSource)
                .withTableName("Photographer")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Photographer create(Photographer photographer) throws DAOException, ValidationException {
        logger.debug("Creating photographer {}", photographer);
        PhotographerValidator.validate(photographer);

        try {
            Map<String, Object> parameters = new HashMap<String, Object>(1);
            parameters.put("name", photographer.getName());
            Number newId = insertPhotographer.executeAndReturnKey(parameters);
            photographer.setId((int) newId.longValue());
            return photographer;
        } catch (DataAccessException ex) {
            logger.error("Failed to create photographer", ex);
            throw new DAOException("Failed to create photographer", ex);
        }
    }

    @Override
    public Photographer getById(int id) throws DAOException {
        try {
            return this.jdbcTemplate.queryForObject(
                    GET_BY_ID_STATEMENT,
                    new Object[]{id},
                    new RowMapper<Photographer>() {
                        @Override
                        public Photographer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Photographer photographer = new Photographer();
                            photographer.setId(rs.getInt(1));
                            photographer.setName(rs.getString(2));
                            return photographer;
                        }
                    });
        } catch (DataAccessException ex) {
            logger.error("Failed to read photographer", ex);
            throw new DAOException("Failed to read photographer", ex);
        }
    }

    public void update(Photographer photographer) throws DAOException, ValidationException {
        logger.debug("Updating photographer {}", photographer);

        PhotographerValidator.validate(photographer);
        PhotographerValidator.validateId(photographer);

        try {
            jdbcTemplate.update(UPDATE_STATEMENT, photographer.getName(), photographer.getId());
        } catch (DataAccessException ex) {
            logger.error("Failed to update photographer", ex);
            throw new DAOException("Failed to update photographer", ex);
        }
    }

    @Override
    public List<Photographer> readAll() throws DAOException {
        try {
            return jdbcTemplate.query(READ_ALL_STATEMENT, new RowMapper<Photographer>() {
                @Override
                public Photographer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Photographer(rs.getInt(1), rs.getString(2));
                }
            });
        } catch (DataAccessException ex) {
            logger.error("Failed to read all photographers", ex);
            throw new DAOException("Failed to read all photographers", ex);
        }
    }
}
