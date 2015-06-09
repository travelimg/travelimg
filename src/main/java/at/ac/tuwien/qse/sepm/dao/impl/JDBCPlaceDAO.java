package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.JourneyDAO;
import at.ac.tuwien.qse.sepm.dao.PlaceDAO;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.validators.PlaceValidator;
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

public class JDBCPlaceDAO extends JDBCDAOBase implements PlaceDAO {
    private static final String readStatement = "SELECT city, country, latitude, longitude, journey_id FROM PLACE WHERE id=?;";
    private static final String readAllStatement = "SELECT id, city, country, latitude, longitude, journey_id FROM PLACE;";
    private static final String deleteStatement = "DELETE FROM PLACE WHERE id=?;";
    private static final String updateStatement = "UPDATE PLACE SET city = ?, country = ?, latitude = ?, longitude = ?, journey_id = ? WHERE id = ?";
    private static final String readByJourneyStatement = "SELECT id, city, country, latitude, longitude FROM PLACE WHERE journey_id=?;";
    @Autowired
    JourneyDAO journeyDAO;
    private SimpleJdbcInsert insertPlace;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertPlace = new SimpleJdbcInsert(dataSource).withTableName("Place")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Place create(Place place) throws DAOException, ValidationException {
        logger.debug("Creating Place", place);

        PlaceValidator.validate(place);

        for (Place element : readAll()) {
            if (element.getCity() == place.getCity() && element.getCountry() == place.getCountry()) return element;
        }

        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("city", place.getCity());
        parameters.put("country", place.getCountry());
        parameters.put("latitude", place.getLatitude());
        parameters.put("longitude", place.getLongitude());
        parameters.put("journey_id", place.getJourney().getId());

        try {
            Number newId = insertPlace.executeAndReturnKey(parameters);
            place.setId((int) newId.longValue());
            return place;
        } catch (DataAccessException ex) {
            logger.error("Failed to create place", ex);
            throw new DAOException("Failed to create place", ex);
        }
    }

    @Override
    public void delete(Place place) throws DAOException, ValidationException {
        logger.debug("Deleting Place", place);

        PlaceValidator.validate(place);
        PlaceValidator.validateID(place.getId());

        try {
            jdbcTemplate.update(deleteStatement, place.getId());
        } catch (DataAccessException ex) {
            logger.error("Failed to delete Place", ex);
            throw new DAOException("Failed to delete Place", ex);
        }
    }

    @Override
    public void update(Place place) throws DAOException, ValidationException {
        logger.debug("Updating Place", place);

        PlaceValidator.validate(place);
        PlaceValidator.validateID(place.getId());

        try {
            jdbcTemplate
                    .update(updateStatement, place.getCity(), place.getCountry(), place.getLatitude(), place.getLongitude(), place.getId());
            logger.debug("Successfully updated Place", place);
        } catch (DataAccessException ex) {
            logger.error("Failed updating Place", place);
            throw new DAOException("Failed updating Place", ex);
        }
    }

    @Override
    public List<Place> readAll() throws DAOException {
        logger.debug("readAll");
        try {
            List<Place> places = jdbcTemplate.query(readAllStatement, new RowMapper<Place>() {

                @Override
                public Place mapRow(ResultSet resultSet, int i) throws SQLException {
                    try {
                        Journey journey = null;
                        if (resultSet.getInt(6) != 0)
                            journey = journeyDAO.getByID(resultSet.getInt(6));

                        return new Place(resultSet.getInt(1), resultSet.getString(2),
                                resultSet.getString(3), resultSet.getDouble(4), resultSet.getDouble(5), journey);
                    } catch (ValidationException | DAOException e) {
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                }
            });

            logger.debug("Successfully read all Places");
            return places;
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all Places", e);
        } catch (RuntimeException ex) {
            throw new DAOException(ex.getCause());
        }
    }

    @Override
    public Place getById(int id) throws DAOException, ValidationException {
        logger.debug("getByID ", id);

        PlaceValidator.validateID(id);

        try {
            return this.jdbcTemplate
                    .queryForObject(readStatement, new Object[]{id}, new RowMapper<Place>() {

                        @Override
                        public Place mapRow(ResultSet resultSet, int i)
                                throws SQLException {
                            try {
                                Journey journey = null;
                                if (resultSet.getInt(5) != 0)
                                    journey = journeyDAO.getByID(resultSet.getInt(5));

                                return new Place(id, resultSet.getString(1),
                                        resultSet.getString(2), resultSet.getDouble(3), resultSet.getDouble(4), journey);
                            } catch (DAOException | ValidationException e) {
                                e.printStackTrace();
                                throw new RuntimeException();
                            }
                        }
                    });
        } catch (DataAccessException ex) {
            logger.error("Failed to read a Place", ex);
            throw new RuntimeException("Failed to read a Place", ex);
        }
    }

    @Override
    public List<Place> readByJourney(Journey journey) throws DAOException, ValidationException {
        logger.debug("readByJourney ", journey.getId());

        try {
            return this.jdbcTemplate.query(readByJourneyStatement, new Object[]{journey.getId()},
                    new RowMapper<Place>() {
                        public Place mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Place place = null;
                            try {
                                place = new Place(rs.getInt(1), rs.getString(2), rs.getString(3),
                                        rs.getDouble(4), rs.getDouble(5), journeyDAO.getByID(rs.getInt(6)));
                            } catch (DAOException | ValidationException e) {
                                e.printStackTrace();
                                throw new RuntimeException();
                            }
                            return place;
                        }
                    });
        } catch (DataAccessException ex) {
            logger.error("Failed to read a Place", ex);
            throw new DAOException("Failed to read a Place", ex);
        }
    }
}
