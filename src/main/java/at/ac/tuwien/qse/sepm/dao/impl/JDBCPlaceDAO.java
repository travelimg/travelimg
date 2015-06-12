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
    private static final String readStatement = "SELECT id, city, country, latitude, longitude, journey_id FROM PLACE WHERE id=?;";
    private static final String readAllStatement = "SELECT id, city, country, latitude, longitude, journey_id FROM PLACE;";
    private static final String updateStatement = "UPDATE PLACE SET city = ?, country = ?, latitude = ?, longitude = ?, journey_id = ? WHERE id = ?";
    private static final String readByJourneyStatement = "SELECT id, city, country, latitude, longitude, journey_id FROM PLACE WHERE journey_id=?;";
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
            if (element.getCity().equals(place.getCity()) && element.getCountry().equals(place.getCountry()))
                return element;
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
    public void update(Place place) throws DAOException, ValidationException {
        logger.debug("Updating Place", place);

        PlaceValidator.validate(place);
        PlaceValidator.validateID(place.getId());

        try {
            jdbcTemplate
                    .update(updateStatement, place.getCity(), place.getCountry(), place.getLatitude(), place.getLongitude(), place.getJourney().getId(), place.getId());
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
            List<Place> places = jdbcTemplate.query(readAllStatement, new PlaceMapper());

            logger.debug("Successfully read all Places");
            return places;
        } catch (DataAccessException | ValidationException.Unchecked | DAOException.Unchecked ex) {
            throw new DAOException("Failed to read all Places", ex);
        }
    }

    @Override
    public Place getById(int id) throws DAOException, ValidationException {
        logger.debug("getByID ", id);

        PlaceValidator.validateID(id);

        try {
            return this.jdbcTemplate.queryForObject(readStatement, new Object[]{id}, new PlaceMapper());
        } catch (DataAccessException | ValidationException.Unchecked | DAOException.Unchecked ex) {
            logger.error("Failed to read a Place", ex);
            throw new DAOException("Failed to read a Place", ex);
        }
    }

    @Override
    public List<Place> readByJourney(Journey journey) throws DAOException, ValidationException {
        logger.debug("readByJourney ", journey.getId());

        try {
            return this.jdbcTemplate.query(readByJourneyStatement, new Object[]{journey.getId()}, new PlaceMapper());
        } catch (DataAccessException | ValidationException.Unchecked | DAOException.Unchecked ex) {
            logger.error("Failed to read a Place", ex);
            throw new DAOException("Failed to read a Place", ex);
        }
    }

    private class PlaceMapper implements RowMapper<Place> {
        @Override
        public Place mapRow(ResultSet rs, int rowNum) throws SQLException {
            Journey journey;

            try {
                journey = journeyDAO.getByID(rs.getInt(6));
            } catch (DAOException ex) {
                throw new DAOException.Unchecked("Failed to fetch journey", ex);
            } catch (ValidationException ex) {
                throw new ValidationException.Unchecked("Failed to validate journey", ex);
            }

            return new Place(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getDouble(4),
                    rs.getDouble(5),
                    journey
            );
        }
    }
}
