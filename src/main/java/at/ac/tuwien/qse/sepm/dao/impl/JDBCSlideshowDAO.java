package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.dao.SlideshowDAO;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.SlideshowValidator;
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

/**
 * Created by mb on 08.06.15.
 */
public class JDBCSlideshowDAO extends JDBCDAOBase implements SlideshowDAO{

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String READ_ALL_STATEMENT = "SELECT id, name, durationbetweenphotos FROM SLIDESHOW;";


    private SimpleJdbcInsert insertSlideshow;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertSlideshow = new SimpleJdbcInsert(dataSource)
                .withTableName("Slideshow")
                .usingGeneratedKeyColumns("id");
    }


    @Override
    public void create(Slideshow slideshow) throws DAOException, ValidationException {
        LOGGER.debug("Entering createSlideshow with {}",slideshow);

        SlideshowValidator.validateID(slideshow.getId());

        logger.debug("Creating slideshow {}",slideshow);
        logger.debug(slideshow.getId());

        try {
            Map<String, Object> parameters = new HashMap<String, Object>(1);
            parameters.put("id", slideshow.getId());
            parameters.put("name", slideshow.getName());
            parameters.put("durationbetweenphotos",slideshow.getDurationBetweenPhotos());
            Number newId = insertSlideshow.executeAndReturnKey(parameters);
            slideshow.setId((int) newId.longValue());
        } catch (DataAccessException ex) {
            logger.error("Failed to create slideshow", ex);
            throw new DAOException("Failed to create slideshow", ex);
        }



        //return null;
    }

    @Override
    public void update(Slideshow slideshow) throws DAOException, ValidationException {

    }

    @Override
    public void delete(Slideshow slideshow) throws DAOException, ValidationException {

    }

    @Override
    public Slideshow getById(int id) throws DAOException {
        return null;
    }

    @Override
    public List<Slideshow> readAll() throws DAOException {
        logger.debug("retrieving all slideshows");

        try {
            return jdbcTemplate.query(READ_ALL_STATEMENT, new RowMapper<Slideshow>() {
                @Override public Slideshow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Slideshow(rs.getInt(1),rs.getString(2),rs.getDouble(3));
                }
            });
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all slides", e);
        } catch (RuntimeException ex) {
            throw new DAOException(ex.getCause());
        }
    }

}
