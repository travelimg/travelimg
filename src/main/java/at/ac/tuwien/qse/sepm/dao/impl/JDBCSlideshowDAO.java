package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.dao.SlideshowDAO;
import at.ac.tuwien.qse.sepm.entities.Slide;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mb on 08.06.15.
 */
public class JDBCSlideshowDAO extends JDBCDAOBase implements SlideshowDAO{

    private static final String READ_ALL_STATEMENT = "SELECT id, name, durationbetweenphotos FROM SLIDESHOW;";
    private static final String UPDATE_STATEMENT = "UPDATE SLIDESHOW SET name = ?, durationbetweenphotos = ? WHERE id = ?;";

    private SimpleJdbcInsert insertSlideshow;

    @Autowired
    private SlideDAO<Slide> slideDAO;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertSlideshow = new SimpleJdbcInsert(dataSource)
                .withTableName("Slideshow")
                .usingGeneratedKeyColumns("id");
    }


    @Override
    public Slideshow create(Slideshow slideshow) throws DAOException, ValidationException {
        logger.debug("Creating slideshow {}", slideshow);

        SlideshowValidator.validate(slideshow);

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", slideshow.getName());
            parameters.put("durationbetweenphotos", slideshow.getDurationBetweenPhotos());
            Number newId = insertSlideshow.executeAndReturnKey(parameters);
            slideshow.setId((int) newId.longValue());

            logger.debug("Created slideshow {}", slideshow);
            return slideshow;
        } catch (DataAccessException ex) {
            logger.error("Failed to create slideshow", ex);
            throw new DAOException("Failed to create slideshow", ex);
        }
    }

    @Override
    public Slideshow update(Slideshow slideshow) throws DAOException, ValidationException {
        logger.debug("Updating slideshow {}", slideshow);

        SlideshowValidator.validate(slideshow);
        SlideshowValidator.validateID(slideshow.getId());

        try {
            jdbcTemplate.update(UPDATE_STATEMENT,
                    slideshow.getName(),
                    slideshow.getDurationBetweenPhotos(),
                    slideshow.getId()
            );
            logger.debug("Successfully updated slideshow");
            return slideshow;
        } catch (DataAccessException ex) {
            logger.error("Failed to update slideshow", ex);
            throw new DAOException("Failed to update slideshow", ex);
        }
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
            return jdbcTemplate.query(READ_ALL_STATEMENT, new SlideshowMapper());
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all slides", e);
        }
    }

    private class SlideshowMapper implements RowMapper<Slideshow> {
        @Override
        public Slideshow mapRow(ResultSet rs, int rowNum) throws SQLException {
            int slideshowId = rs.getInt(1);
            List<Slide> slides;

            try {
                slides = slideDAO.getSlidesForSlideshow(slideshowId);
            } catch (DAOException | ValidationException ex) {
                throw new DAOException.Unchecked("Failed to retrieve slides for given slideshow", ex);
            }

            return new Slideshow(
                    slideshowId,
                    rs.getString(2),
                    rs.getDouble(3),
                    slides
            );
        }
    }
}
