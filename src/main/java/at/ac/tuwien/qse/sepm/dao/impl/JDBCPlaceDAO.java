package at.ac.tuwien.qse.sepm.dao.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.EntityWatcher;
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
import java.util.*;
import java.util.function.Consumer;

public class JDBCPlaceDAO extends JDBCDAOBase implements PlaceDAO, EntityWatcher<Place> {
    private static final String readStatement = "SELECT id, city, country, latitude, longitude FROM PLACE WHERE id=?;";
    private static final String readAllStatement = "SELECT id, city, country, latitude, longitude FROM PLACE;";
    private static final String updateStatement = "UPDATE PLACE SET city = ?, country = ?, latitude = ?, longitude = ? WHERE id = ?";
    private static final String readByCountryCityStatement = "SELECT id, city, country, latitude, longitude FROM PLACE WHERE country=? AND city=?;";

    private SimpleJdbcInsert insertPlace;

    private Collection<Consumer<Place>> addedCallbacks = new ArrayList<>();

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

        try {
            Number newId = insertPlace.executeAndReturnKey(parameters);
            place.setId((int) newId.longValue());

            addedCallbacks.forEach(cb -> cb.accept(place));

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
            List<Place> places = jdbcTemplate.query(readAllStatement, new PlaceMapper());

            logger.debug("Successfully read all Places");
            return places;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to read all Places", ex);
        }
    }

    @Override
    public Place getById(int id) throws DAOException, ValidationException {
        logger.debug("getByID ", id);

        PlaceValidator.validateID(id);

        try {
            return this.jdbcTemplate.queryForObject(readStatement, new Object[]{id}, new PlaceMapper());
        } catch (DataAccessException ex) {
            logger.error("Failed to read a Place", ex);
            throw new DAOException("Failed to read a Place", ex);
        }
    }

    @Override public Place readByCountryCity(String country, String city) throws DAOException {
        if (country == null) throw new IllegalArgumentException();
        if (city == null) throw new IllegalArgumentException();

        try {
            return this.jdbcTemplate.queryForObject(readByCountryCityStatement, new Object[]{country, city}, new PlaceMapper());
        } catch (DataAccessException ex) {
            logger.error("Failed to read a Place", ex);
            throw new DAOException("Failed to read a Place", ex);
        }
    }

    @Override public void subscribeAdded(Consumer<Place> callback) {
        addedCallbacks.add(callback);
    }


    private class PlaceMapper implements RowMapper<Place> {
        @Override
        public Place mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Place(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getDouble(4),
                    rs.getDouble(5)
            );
        }
    }
}
