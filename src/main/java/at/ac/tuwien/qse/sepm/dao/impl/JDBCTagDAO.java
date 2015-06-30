package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.EntityWatcher;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.dao.TagDAO;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.TagValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class JDBCTagDAO extends JDBCDAOBase implements TagDAO, EntityWatcher<Tag> {

    private static final Logger logger = LogManager.getLogger();
    private static final int MAX_TAG_LENGTH = 30;

    private static final String readStatement = "SELECT ID,name FROM TAG WHERE ID=?;";
    private static final String readNameStatement = "SELECT ID,name FROM TAG WHERE NAME=?;";
    private static final String readAllStatement = "SELECT ID,name FROM TAG;";
    private static final String deleteStatement = "DELETE FROM TAG WHERE ID=?;";

    private SimpleJdbcInsert insertTag;

    @Autowired
    private PhotoTagDAO photoTagDao;

    private Collection<Consumer<Tag>> addedCallbacks = new ArrayList<>();

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertTag = new SimpleJdbcInsert(dataSource).withTableName("Tag")
                .usingGeneratedKeyColumns("id");
    }

    /**
     * Create a Tag in the data store
     *
     * @param t Tag which to create
     * @return the created Tag
     * @throws DAOException If the Tag can not be created or the data store fails to create a record.
     */
    public Tag create(Tag t) throws DAOException, ValidationException {
        logger.debug("Creating Tag {}", t);
        TagValidator.validate(t);

        if (exists(t)) {
            throw new ValidationException("Tag already exists");
        }

        try {
            Map<String, Object> parameters = new HashMap<String, Object>(1);
            String name = t.getName();
            name = name.substring(0, Math.min(name.length(), MAX_TAG_LENGTH));
            parameters.put("name", name);
            Number newId = insertTag.executeAndReturnKey(parameters);
            t.setId((int) newId.longValue());

            addedCallbacks.forEach(cb -> cb.accept(t));

            return t;
        } catch (DataAccessException e) {
            throw new DAOException("Failed to create Tag", e);
        }
    }

    /**
     * Retrieve an existing Tag
     *
     * @param t Specifies which Tag to retrieve by providing the id.
     * @return the Tag-Objekt
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     */
    public Tag read(Tag t) throws DAOException {
        logger.debug("reading Tag {}", t);
        try {
            return this.jdbcTemplate.queryForObject(readStatement, new Object[]{t.getId()},
                    new RowMapper<Tag>() {
                        @Override
                        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Tag t = new Tag();
                            t.setId(rs.getInt(1));
                            t.setName(rs.getString(2));
                            return t;
                        }
                    });
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read a Tag", e);
        }
    }

    /**
     * Retrieve an existing Tag
     *
     * @param t Specifies which Tag to retrieve by providing the name.
     * @return the Tag-Objekt
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     */
    public Tag readName(Tag t) throws DAOException {
        logger.debug("reading Tagname {}", t);
        try {
            return this.jdbcTemplate.queryForObject(readNameStatement, new Object[]{t.getName()},
                    new RowMapper<Tag>() {
                        @Override
                        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Tag t = new Tag();
                            t.setId(rs.getInt(1));
                            t.setName(rs.getString(2));
                            return t;
                        }
                    });
        } catch (DataAccessException e) {
            logger.error("failed to read tag {}", e);
            throw new DAOException("Failed to read a Tag", e);
        }
    }

    @Override
    public boolean exists(Tag tag) throws DAOException {
        logger.debug("exists for {}", tag);

        try {
            return this.jdbcTemplate.queryForObject(readNameStatement, new Object[]{tag.getName()},
                    (rs, rowNum) -> true
            );
        } catch (EmptyResultDataAccessException ex) {
            return false;
        } catch (DataAccessException ex) {
            logger.error("Failed to retrieve tag", ex);
            throw new DAOException("Failed to retrieve tag", ex);
        }
    }

    /**
     * Delete a Tag
     *
     * @param t Specifies which Tag to delete by providing the id.
     * @throws DAOException
     */
    public void delete(Tag t) throws DAOException {
        logger.debug("Delete Tag {}", t);
        try {
            photoTagDao.deleteAllEntriesOfSpecificTag(t);
            jdbcTemplate.update(deleteStatement, t.getId());
        } catch (DataAccessException e) {
            throw new DAOException("Failed to delete Tag ", e);
        } catch (ValidationException e) {
            throw new DAOException("Failed to validate entity", e);
        }
    }

    /**
     * Retrieve a list of all existing Tags
     *
     * @return the list of Tags
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     */
    public List<Tag> readAll() throws DAOException {
        logger.debug("reading all Tag's {}");
        try {
            return jdbcTemplate.query(readAllStatement, new RowMapper<Tag>() {
                @Override
                public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Tag(rs.getInt(1), rs.getString(2));
                }
            });
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all Tag's", e);
        }
    }

    @Override public void subscribeAdded(Consumer<Tag> callback) {
        addedCallbacks.add(callback);
    }
}
