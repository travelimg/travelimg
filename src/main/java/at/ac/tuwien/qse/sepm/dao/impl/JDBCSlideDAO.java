package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.dao.SlideshowDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.SlideValidator;
import at.ac.tuwien.qse.sepm.entities.validators.SlideshowValidator;
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


public class JDBCSlideDAO extends JDBCDAOBase implements SlideDAO {

    private static final String READ_ALL_STATEMENT = "SELECT id, photo_id, slideshow_id, orderposition FROM SLIDE ORDER BY orderposition asc;";
    private static final String READ_ALL_BY_SLIDESHOW_STATEMENT = "SELECT id, photo_id, slideshow_id, orderposition FROM SLIDE WHERE slideshow_id=? ORDER BY orderposition asc;";
    private static final String UPDATE_STATEMENT = "UPDATE SLIDE SET photo_id = ?, slideshow_id = ?, orderposition = ? WHERE id = ?;";

    private SimpleJdbcInsert insertSlide;

    @Autowired
    private PhotoDAO photoDAO;

    @Autowired
    private SlideshowDAO slideshowDAO;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertSlide = new SimpleJdbcInsert(dataSource)
                .withTableName("Slide")
                .usingGeneratedKeyColumns("id");
    }


    @Override public Slide create(Slide slide) throws DAOException, ValidationException {
        logger.debug("Creating slide {}",slide);

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("id", slide.getId());
            parameters.put("photo_id", slide.getPhoto().getId());
            parameters.put("slideshow_id",slide.getSlideshowId());
            parameters.put("orderposition",slide.getOrder());
            Number newId = insertSlide.executeAndReturnKey(parameters);
            slide.setId((int) newId.longValue());

            return slide;
        } catch (DataAccessException ex) {
            logger.error("Failed to create slide", ex);
            throw new DAOException("Failed to create slide", ex);
        }
    }

    @Override public void delete(Slide slide) throws DAOException, ValidationException {

    }

    @Override
    public Slide update(Slide slide) throws DAOException, ValidationException {
        logger.debug("Updating slide {}", slide);

        SlideValidator.validate(slide);
        SlideValidator.validateID(slide.getId());

        try {
            jdbcTemplate.update(UPDATE_STATEMENT,
                    slide.getPhoto().getId(),
                    slide.getSlideshowId(),
                    slide.getOrder(),
                    slide.getId()
            );
            logger.debug("Successfully updated slide");
            return slide;
        } catch (DataAccessException ex) {
            logger.error("Failed to update slide", ex);
            throw new DAOException("Failed to update slide", ex);
        }
    }

    @Override
    public List<Slide> getSlidesForSlideshow(int slideshowId) throws DAOException, ValidationException {
        logger.debug("Retrieving slides for slideshow with id {}", slideshowId);

        SlideValidator.validateID(slideshowId);

        try {
            return jdbcTemplate.query(READ_ALL_BY_SLIDESHOW_STATEMENT,
                    new SlideMapper(), slideshowId);
        } catch (DataAccessException | DAOException.Unchecked ex) {
            logger.error("Failed to retrieve slides for given slideshow", ex);
            throw new DAOException("Failed to retrieve slides for given slideshow", ex);
        }
    }

    @Override public List<Slide> readAll() throws DAOException {
        logger.debug("retrieving all slides");

        try {
            return jdbcTemplate.query(READ_ALL_STATEMENT, new SlideMapper());
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all slides", e);
        } catch (DAOException.Unchecked ex) {
            logger.error("Failed to read all slides", ex);
            throw new DAOException("Failed to read all slides", ex);
        }
    }

    private class SlideMapper implements RowMapper<Slide> {
        @Override
        public Slide mapRow(ResultSet rs, int rowNum) throws SQLException {
            int photoId = rs.getInt(2);

            Photo photo;

            try {
                photo = photoDAO.getById(photoId);
            } catch (DAOException | ValidationException ex) {
                throw new DAOException.Unchecked("Failed to retrieve photo with id " + photoId, ex);
            }

            return new Slide(rs.getInt(1), photo, rs.getInt(3), rs.getInt(4));
        }
    }
}
