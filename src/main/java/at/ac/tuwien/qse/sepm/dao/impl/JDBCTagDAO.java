package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.TagDAO;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.TagValidator;
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

public class JDBCTagDAO extends JDBCDAOBase implements TagDAO {

    private static final Logger logger = LogManager.getLogger();

    private static final String readStatement = "SELECT ID,name FROM TAG WHERE ID=?;";
    private static final String readAllStatement = "SELECT ID,name FROM TAG;";
    private static final String deleteStatement ="DELETE FROM TAG WHERE ID=?;";


    private SimpleJdbcInsert insertTag;


    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertTag = new SimpleJdbcInsert(dataSource)
                .withTableName("Tag")
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
        logger.debug("Creating Tag {}",t);
        TagValidator.validate(t);
        try {
            Map<String, Object> parameters = new HashMap<String, Object>(1);
            parameters.put("name", t.getName());
            Number newId = insertTag.executeAndReturnKey(parameters);
            t.setId((int) newId.longValue());
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
        logger.debug("reading Tag {}",t);
        try {
            return this.jdbcTemplate.queryForObject(
                    readStatement,
                    new Object[]{t.getId()},
                    new RowMapper<Tag>() {
                        @Override
                        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Tag t = new Tag();
                            t.setId(rs.getInt(1));
                            t.setName(rs.getString(2));
                            return t;
                        }
                    });
        }
        catch(DataAccessException e){
            throw new DAOException("Failed to read a Tag", e);
        }
    }

    /**
     * Delete a Tag
     *
     * @param t Specifies which Tag to delete by providing the id.
     * @throws DAOException
     */
    public void delete(Tag t) throws DAOException {
        logger.debug("Delete Tag {}",t);
        try {
        jdbcTemplate.update(deleteStatement,t.getId());
        }catch (DataAccessException e){
            throw new DAOException("Failed to delete Tag ", e);
        }
    }

    /**
     *
     * Retrieve a list of all existing Tags
     *
     * @return the list of Tags
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     *
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
        }
        catch(DataAccessException e){
            throw new DAOException("Failed to read all Tag's", e);
        }
    }
}
