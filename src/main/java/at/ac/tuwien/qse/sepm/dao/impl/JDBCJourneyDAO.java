package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.JourneyDAO;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.validators.JourneyValidator;
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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCJourneyDAO extends JDBCDAOBase implements JourneyDAO {
    private static final Logger logger = LogManager.getLogger(JDBCJourneyDAO.class);

    private static final String readStatement = "SELECT name, start, end FROM JOURNEY WHERE id=?;";
    private static final String readAllStatement = "SELECT id, name, start, end FROM JOURNEY;";
    private static final String deleteStatement = "DELETE FROM JOURNEY WHERE id=?;";
    private static final String updateStatement = "UPDATE Journey SET name = ?, start = ?, end = ? WHERE id = ?";
    private SimpleJdbcInsert insertJourney;

    @Override @Autowired public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertJourney = new SimpleJdbcInsert(dataSource).withTableName("Journey")
                .usingGeneratedKeyColumns("id");
    }

    @Override public Journey create(Journey journey) throws DAOException, ValidationException {
        logger.debug("Creating Journey", journey);
        if (journey == null)
            throw new IllegalArgumentException();
        // TODO: handle validator exception
        JourneyValidator.validate(journey);

        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("id", journey.getId());
        parameters.put("name", journey.getName());
        parameters.put("start", Timestamp.valueOf(journey.getStartDate()));
        parameters.put("end", Timestamp.valueOf(journey.getEndDate()));

        try {
            insertJourney.execute(parameters);
            return journey;
        } catch (DataAccessException ex) {
            logger.error("Failed to create journey", ex);
            throw new DAOException("Failed to create journey", ex);
        }

    }

    @Override public void delete(Journey journey) throws DAOException, ValidationException {
        logger.debug("Deleting Journey", journey);
        if (journey == null)
            throw new IllegalArgumentException();
        try {
            jdbcTemplate.update(deleteStatement, journey.getId());
        } catch (DataAccessException ex) {
            logger.error("Failed to delete Journey", ex);
            throw new DAOException("Failed to delete Journey", ex);
        }
    }

    @Override public void update(Journey journey) throws DAOException, ValidationException {
        logger.debug("Updating Journey", journey);
        if (journey == null)
            throw new IllegalArgumentException();

        JourneyValidator.validate(journey);

        try {
            jdbcTemplate.update(updateStatement, journey.getName(), journey.getStartDate(), journey.getEndDate(),
                    journey.getId());
            logger.debug("Successfully updated Journey", journey);
        } catch (DataAccessException ex) {
            logger.error("Failed updating Journey", journey);
            throw new DAOException("Failed updating Journey", ex);
        }
    }

    @Override public List<Journey> readAll() throws DAOException {
        logger.debug("readAll");
        try {
            List<Journey> journeys = jdbcTemplate.query(readAllStatement, new RowMapper<Journey>() {

                @Override public Journey mapRow(ResultSet resultSet, int i) throws SQLException {
                    return new Journey(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getTimestamp(3).toLocalDateTime(),
                            resultSet.getTimestamp(4).toLocalDateTime());
                }
            });

            logger.debug("Successfully read all journeys");
            return journeys;
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all journeys", e);
        } catch (RuntimeException ex) {
            throw new DAOException(ex.getCause());
        }
    }

    @Override public Journey getByName(String name) throws DAOException {
        logger.debug("getByName ", name);
        if (name == null)
            throw new IllegalArgumentException();

        try {
            return this.jdbcTemplate
                    .queryForObject(readStatement, new Object[] { name }, new RowMapper<Journey>() {

                        @Override public Journey mapRow(ResultSet resultSet, int i)
                                throws SQLException {
                            return new Journey(resultSet.getInt(1), resultSet.getString(2),
                                    resultSet.getTimestamp(3).toLocalDateTime(),
                                    resultSet.getTimestamp(4).toLocalDateTime());
                        }
                    });
        }catch (DataAccessException ex) {
            throw new DAOException("Failed to read a Journey", ex);
        }
    }
}

