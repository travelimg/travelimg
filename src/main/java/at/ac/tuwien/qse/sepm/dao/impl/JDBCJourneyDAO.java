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
import java.util.*;
import java.util.function.Consumer;

public class JDBCJourneyDAO extends JDBCDAOBase implements JourneyDAO, EntityWatcher<Journey> {
    private static final Logger logger = LogManager.getLogger(JDBCJourneyDAO.class);

    private static final String readByNameStatement = "SELECT id, name, start, end FROM JOURNEY WHERE name=?;";
    private static final String readStatement = "SELECT id, name, start, end FROM JOURNEY WHERE id=?;";
    private static final String readAllStatement = "SELECT id, name, start, end FROM JOURNEY;";
    private static final String deleteStatement = "DELETE FROM JOURNEY WHERE id=?;";
    private static final String updateStatement = "UPDATE Journey SET name = ?, start = ?, end = ? WHERE id = ?";
    private SimpleJdbcInsert insertJourney;

    private Collection<Consumer<Journey>> addedCallbacks = new ArrayList<>();

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertJourney = new SimpleJdbcInsert(dataSource).withTableName("Journey")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Journey create(Journey journey) throws DAOException, ValidationException {
        logger.debug("Creating Journey", journey);

        JourneyValidator.validate(journey);

        for (Journey element : readAll()) {
            if (element.getName().equals(journey.getName()) && element.getStartDate() == journey
                    .getStartDate() && element.getEndDate() == journey.getStartDate())
                return element;
        }

        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("name", journey.getName());
        parameters.put("start", Timestamp.valueOf(journey.getStartDate()));
        parameters.put("end", Timestamp.valueOf(journey.getEndDate()));

        try {
            Number newId = insertJourney.executeAndReturnKey(parameters);
            journey.setId((int) newId.longValue());

            addedCallbacks.forEach(cb -> cb.accept(journey));

            return journey;
        } catch (DataAccessException ex) {
            logger.error("Failed to create journey", ex);
            throw new DAOException("Failed to create journey", ex);
        }
    }

    @Override
    public void delete(Journey journey) throws DAOException, ValidationException {
        logger.debug("Deleting Journey", journey);

        JourneyValidator.validateID(journey.getId());

        try {
            jdbcTemplate.update(deleteStatement, journey.getId());
        } catch (DataAccessException ex) {
            logger.error("Failed to delete Journey", ex);
            throw new DAOException("Failed to delete Journey", ex);
        }
    }

    @Override
    public void update(Journey journey) throws DAOException, ValidationException {
        logger.debug("Updating Journey", journey);

        JourneyValidator.validateID(journey.getId());
        JourneyValidator.validate(journey);

        try {
            jdbcTemplate.update(updateStatement,
                    journey.getName(),
                    Timestamp.valueOf(journey.getStartDate()),
                    Timestamp.valueOf(journey.getEndDate()),
                    journey.getId());
            logger.debug("Successfully updated Journey", journey);
        } catch (DataAccessException ex) {
            logger.error("Failed updating Journey", journey);
            throw new DAOException("Failed updating Journey", ex);
        }
    }

    @Override
    public List<Journey> readAll() throws DAOException {
        logger.debug("readAll");
        try {
            List<Journey> journeys = jdbcTemplate.query(readAllStatement, new JourneyMapper());
            logger.debug("Successfully read all journeys");
            return journeys;
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all journeys", e);
        }
    }

    @Override
    public Journey getByID(int id) throws DAOException, ValidationException {
        logger.debug("getByID({})", id);

        JourneyValidator.validateID(id);

        try {
            return this.jdbcTemplate.queryForObject(readStatement, new Object[]{id}, new JourneyMapper());
        } catch (DataAccessException ex) {
            logger.debug("Failed to read a Journey");
            return null;
        }
    }

    @Override
    public Journey getByName(String name) throws DAOException {
        if (name == null) throw new IllegalArgumentException();
        logger.debug("getByName({})", name);
        try {
            return this.jdbcTemplate.queryForObject(readByNameStatement, new Object[]{name}, new JourneyMapper());
        } catch (DataAccessException ex) {
            logger.debug("Failed to read a Journey");
            throw new DAOException(ex);
        }
    }

    @Override public void subscribeAdded(Consumer<Journey> callback) {
        addedCallbacks.add(callback);
    }

    private class JourneyMapper implements RowMapper<Journey> {
        @Override
        public Journey mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Journey(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getTimestamp(3).toLocalDateTime(),
                    rs.getTimestamp(4).toLocalDateTime()
            );
        }
    }
}

