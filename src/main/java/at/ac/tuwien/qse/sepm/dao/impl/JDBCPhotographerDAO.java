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
import java.util.*;
import java.util.function.Consumer;

public class JDBCPhotographerDAO extends JDBCDAOBase implements PhotographerDAO,
        EntityWatcher<Photographer> {

    private static final Logger logger = LogManager.getLogger();

    private static final String UPDATE_STATEMENT = "UPDATE Photographer SET name=? WHERE id=?";
    private static final String GET_BY_ID_STATEMENT = "SELECT ID, name FROM Photographer WHERE ID=?;";
    private static final String GET_BY_NAME_STATEMENT = "SELECT ID, name FROM Photographer WHERE name=?;";
    private static final String READ_ALL_STATEMENT = "SELECT ID, name FROM Photographer;";

    private SimpleJdbcInsert insertPhotographer;

    private Collection<Consumer<Photographer>> addedCallbacks = new ArrayList<>();

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
            Map<String, Object> parameters = new HashMap<>(1);
            parameters.put("name", photographer.getName());
            Number newId = insertPhotographer.executeAndReturnKey(parameters);
            photographer.setId((int) newId.longValue());

            addedCallbacks.forEach(cb -> cb.accept(photographer));

            return photographer;
        } catch (DataAccessException ex) {
            logger.error("Failed to create photographer", ex);
            throw new DAOException("Failed to create photographer", ex);
        }
    }

    @Override
    public Photographer getById(int id) throws DAOException {
        try {
            return this.jdbcTemplate.queryForObject(GET_BY_ID_STATEMENT, new Object[]{id}, new PhotographerMapper());
        } catch (DataAccessException ex) {
            logger.error("Failed to read photographer", ex);
            throw new DAOException("Failed to read photographer", ex);
        }
    }

    @Override public Photographer getByName(String name) throws DAOException {
        if (name == null) throw new IllegalArgumentException();
        try {
            return this.jdbcTemplate.queryForObject(GET_BY_NAME_STATEMENT, new Object[]{name}, new PhotographerMapper());
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
            return jdbcTemplate.query(READ_ALL_STATEMENT, new PhotographerMapper());
        } catch (DataAccessException ex) {
            logger.error("Failed to read all photographers", ex);
            throw new DAOException("Failed to read all photographers", ex);
        }
    }

    @Override public void subscribeAdded(Consumer<Photographer> callback) {
        addedCallbacks.add(callback);
    }


    private class PhotographerMapper implements RowMapper<Photographer> {
        @Override
        public Photographer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Photographer(
                    rs.getInt(1),
                    rs.getString(2)
            );
        }
    }
}
