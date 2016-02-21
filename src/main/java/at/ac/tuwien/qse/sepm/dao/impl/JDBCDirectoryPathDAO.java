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
import at.ac.tuwien.qse.sepm.dao.DirectoryPathDAO;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

/**
 * Database-based implementation of DirectoryPathDAO.
 */
public class JDBCDirectoryPathDAO extends JDBCDAOBase implements DirectoryPathDAO {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String READ_STATEMENT = "SELECT path from WorkspaceDirectory";
    private static final String DELETE_STATEMENT = "DELETE FROM WorkspaceDirectory "
            + "WHERE path = ?";

    private SimpleJdbcInsert insertDirectory;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertDirectory = new SimpleJdbcInsert(dataSource)
                .withTableName("WorkspaceDirectory");
    }

    /**
     * Add a new path entry for a valid and existing directory to workspace.
     *
     * @param directory must be a valid path to an existing directory
     * @throws DAOException        if operation fails
     */
    @Override public void create(Path directory) throws DAOException {
        LOGGER.debug("Adding workspace directory {}", directory);

        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("path", directory);

        try {
            insertDirectory.execute(parameters);
            LOGGER.info("Successfully added directory to workspace: {}", directory);
        } catch (DataAccessException ex) {
            logger.error("Failed to create entry for given directory", ex);
            throw new DAOException("Failed to create entry for given directory", ex);
        }
    }

    /**
     * Read all current workspace directories.
     *
     * @return Collection with all current workspace directories.
     * @throws DAOException if operation fails
     */
    @Override public Collection<Path> read() throws DAOException {
        LOGGER.debug("Retrieving workspace directories.");

        try {
            return jdbcTemplate.query(READ_STATEMENT, new PathRowMapper());
        } catch (DataAccessException ex) {
            LOGGER.error("Retrieval of workspace directories failed");
            throw new DAOException(ex);
        }
    }

    /**
     * Delete a current workspace directory.
     * If the given Path does not lead to a current workspace directory, the workspace will
     * remain unchanged.
     *
     * @param directory must be a valid path to an existing directory
     * @throws DAOException        if operation fails
     */
    @Override public void delete(Path directory) throws DAOException {
        LOGGER.debug("Deleting directory {}", directory);

        try {
            String pathString = directory.toString();
            jdbcTemplate.update(DELETE_STATEMENT, pathString);
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to delete delete directory", ex);
        }
    }

    private class PathRowMapper implements RowMapper<Path> {
        @Override
        public Path mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Paths.get(rs.getString("path"));
        }
    }
}
