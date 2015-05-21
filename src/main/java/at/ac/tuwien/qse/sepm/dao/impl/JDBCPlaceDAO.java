package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PlaceDAO;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
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

/**
 * Created by David on 21.05.2015.
 */
public class JDBCPlaceDAO extends JDBCDAOBase implements PlaceDAO {
    private static final String readStatement = "SELECT city, country FROM PLACE WHERE id=?;";
    private static final String readAllStatement = "SELECT city, country, FROM PLACE;";
    private static final String deleteStatement = "DELETE FROM PLACE WHERE id=?;";
    private static final String updateStatement = "UPDATE PLACE SET city = ?, country = ? WHERE id = ?";
    private SimpleJdbcInsert insertJourney;

    @Override @Autowired public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertJourney = new SimpleJdbcInsert(dataSource).withTableName("Place")
                .usingGeneratedKeyColumns("id");
    }

    @Override public Place create(Place place) throws DAOException, ValidationException {
        logger.debug("Creating Place", place);
        if (place == null)
            throw new IllegalArgumentException();

        // TODO: Validator
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("id", place.getId());
        parameters.put("city", place.getCity());
        parameters.put("country", place.getCountry());

        try {
            insertJourney.execute(parameters);
            return place;
        } catch (DataAccessException ex) {
            logger.error("Failed to create place", ex);
            throw new DAOException("Failed to create place", ex);
        }
    }

    @Override public void delete(Place place) throws DAOException, ValidationException {
        logger.debug("Deleting Place", place);
        if (place == null)
            throw new IllegalArgumentException();
        try {
            jdbcTemplate.update(deleteStatement, place.getId());
        } catch (DataAccessException ex) {
            logger.error("Failed to delete Place", ex);
            throw new DAOException("Failed to delete Place", ex);
        }
    }

    @Override public void update(Place place) throws DAOException, ValidationException {
        logger.debug("Updating Place", place);
        if (place == null)
            throw new IllegalArgumentException();

        try {
            jdbcTemplate
                    .update(updateStatement, place.getCity(), place.getCountry(), place.getId());
            logger.debug("Successfully updated Place", place);
        } catch (DataAccessException ex) {
            logger.error("Failed updating Place", place);
            throw new DAOException("Failed updating Place", ex);
        }
    }

    @Override public List<Place> readAll() throws DAOException {
        logger.debug("readAll");
        try {
            List<Place> journeys = jdbcTemplate.query(readAllStatement, new RowMapper<Place>() {

                @Override public Place mapRow(ResultSet resultSet, int i) throws SQLException {
                    return new Place(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3));
                }
            });

            logger.debug("Successfully read all Places");
            return journeys;
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all Places", e);
        } catch (RuntimeException ex) {
            throw new DAOException(ex.getCause());
        }
    }

    @Override public Place getByCityName(String name) throws DAOException {
        logger.debug("getByName ", name);
        if (name == null)
            throw new IllegalArgumentException();

        try {
            return this.jdbcTemplate
                    .queryForObject(readStatement, new Object[] { name }, new RowMapper<Place>() {

                        @Override public Place mapRow(ResultSet resultSet, int i)
                                throws SQLException {
                            return new Place(resultSet.getInt(1), resultSet.getString(2),
                                    resultSet.getString(3));
                        }
                    });
        } catch (DataAccessException ex) {
            logger.error("Failed to read a Place", ex);
            throw new DAOException("Failed to read a Place", ex);
        }
    }
}
