package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.SlideValidator;
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

public class JDBCSlideDAO extends JDBCDAOBase implements SlideDAO {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String BASE_UPDATE_STATEMENT = "UPDATE SLIDE SET slideshow_id = ?, orderposition = ?, caption = ? WHERE id = ?;";
    private static final String MAP_INSERT_STATEMENT = "INSERT INTO MapSlide(id, latitude, longitude, zoomLevel) VALUES(?, ?, ?, ?)";
    private static final String MAP_UPDATE_STATEMENT = "UPDATE MapSlide SET latitude = ?, longitude = ?, zoomLevel = ?  WHERE id = ?;";
    private static final String MAP_DELETE_STATEMENT = "DELETE FROM MapSlide where id = ?;";
    private static final String MAP_READ_ALL_BY_SLIDESHOW_STATEMENT = "SELECT Slide.id, slideshow_id, orderposition, caption, latitude, longitude, zoomLevel FROM MapSlide JOIN Slide ON MapSlide.id = Slide.id WHERE slideshow_id=? ORDER BY orderposition asc;";
    private static final String TITLE_INSERT_STATEMENT = "INSERT INTO TitleSlide(id, color) VALUES(?, ?)";
    private static final String TITLE_UPDATE_STATEMENT = "UPDATE TitleSlide SET color = ?  WHERE id = ?;";
    private static final String TITLE_READ_ALL_BY_SLIDESHOW_STATEMENT = "SELECT Slide.id, slideshow_id, orderposition, caption, color FROM TitleSlide JOIN Slide ON TitleSlide.id = Slide.id WHERE slideshow_id=? ORDER BY orderposition asc;";
    private static final String PHOTO_INSERT_STATEMENT = "INSERT INTO PhotoSlide(id, photo_id) VALUES(?, ?)";
    private static final String PHOTO_UPDATE_STATEMENT = "UPDATE PhotoSlide SET photo_id = ?  WHERE id = ?;";
    private static final String PHOTO_READ_ALL_BY_SLIDESHOW_STATEMENT = "SELECT Slide.id, slideshow_id, orderposition, caption, photo_id FROM PhotoSlide JOIN Slide ON PhotoSlide.id = Slide.id WHERE slideshow_id=? ORDER BY orderposition asc;";

    @Autowired private PhotoDAO photoDAO;

    private SimpleJdbcInsert insertSlide;

    @Override @Autowired public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertSlide = new SimpleJdbcInsert(dataSource).withTableName("Slide")
                .usingGeneratedKeyColumns("id");
    }

    @Override public PhotoSlide create(PhotoSlide slide) throws DAOException, ValidationException {
        createBase(slide);

        try {
            jdbcTemplate.update(PHOTO_INSERT_STATEMENT, slide.getId(), slide.getPhoto().getId());
            return slide;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to create photo slide", ex);
        }
    }

    @Override public MapSlide create(MapSlide slide) throws DAOException, ValidationException {
        createBase(slide);

        try {
            jdbcTemplate.update(MAP_INSERT_STATEMENT, slide.getId(), slide.getLatitude(),
                    slide.getLongitude(), slide.getZoomLevel());
            return slide;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to create map slide", ex);
        }
    }

    @Override public TitleSlide create(TitleSlide slide) throws DAOException, ValidationException {
        createBase(slide);

        try {
            jdbcTemplate.update(TITLE_INSERT_STATEMENT, slide.getId(), slide.getColor());
            return slide;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to create title slide", ex);
        }
    }

    @Override public PhotoSlide update(PhotoSlide slide) throws DAOException, ValidationException {
        logger.debug("Updating photo slide {}", slide);

        updateBase(slide);

        try {
            jdbcTemplate.update(PHOTO_UPDATE_STATEMENT, slide.getPhoto().getId(), slide.getId());
            logger.debug("Successfully updated photo slide");
            return slide;
        } catch (DataAccessException ex) {
            logger.error("Failed to update photo slide", ex);
            throw new DAOException("Failed to update photo slide", ex);
        }
    }

    @Override public MapSlide update(MapSlide slide) throws DAOException, ValidationException {
        logger.debug("Updating map slide {}", slide);

        updateBase(slide);

        try {
            jdbcTemplate.update(MAP_UPDATE_STATEMENT, slide.getLatitude(), slide.getLongitude(),
                    slide.getZoomLevel(), slide.getId());
            logger.debug("Successfully updated map slide");
            return slide;
        } catch (DataAccessException ex) {
            logger.error("Failed to update map slide", ex);
            throw new DAOException("Failed to update map slide", ex);
        }
    }

    @Override public TitleSlide update(TitleSlide slide) throws DAOException, ValidationException {
        logger.debug("Updating title slide {}", slide);

        updateBase(slide);

        try {
            jdbcTemplate.update(TITLE_UPDATE_STATEMENT, slide.getColor(), slide.getId());
            logger.debug("Successfully updated title slide");
            return slide;
        } catch (DataAccessException ex) {
            logger.error("Failed to update title slide", ex);
            throw new DAOException("Failed to update title slide", ex);
        }
    }

    @Override public void delete(PhotoSlide slide) throws DAOException, ValidationException {

    }

    @Override public void delete(MapSlide slide) throws DAOException, ValidationException {

    }

    @Override public void delete(TitleSlide slide) throws DAOException, ValidationException {

    }

    @Override public List<PhotoSlide> getPhotoSlidesForSlideshow(int slideshowId)
            throws DAOException {
        try {
            return jdbcTemplate.query(PHOTO_READ_ALL_BY_SLIDESHOW_STATEMENT, new PhotoSlideMapper(),
                    slideshowId);
        } catch (DataAccessException | DAOException.Unchecked ex) {
            throw new DAOException("Failed to retrieve photo slides for given slideshow", ex);
        }
    }

    @Override public List<MapSlide> getMapSlidesForSlideshow(int slideshowId) throws DAOException {
        try {
            return jdbcTemplate
                    .query(MAP_READ_ALL_BY_SLIDESHOW_STATEMENT, new MapSlideMapper(), slideshowId);
        } catch (DataAccessException | DAOException.Unchecked ex) {
            throw new DAOException("Failed to retrieve map slides for given slideshow", ex);
        }
    }

    @Override public List<TitleSlide> getTitleSlidesForSlideshow(int slideshowId)
            throws DAOException {
        try {
            return jdbcTemplate.query(TITLE_READ_ALL_BY_SLIDESHOW_STATEMENT, new TitleSlideMapper(),
                    slideshowId);
        } catch (DataAccessException | DAOException.Unchecked ex) {
            throw new DAOException("Failed to retrieve title slides for given slideshow", ex);
        }
    }

    private Slide createBase(Slide slide) throws DAOException, ValidationException {
        logger.debug("Creating slide {}", slide);

        SlideValidator.validate(slide);

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("id", slide.getId());
            parameters.put("slideshow_id", slide.getSlideshowId());
            parameters.put("orderposition", slide.getOrder());
            parameters.put("caption", slide.getCaption());
            Number newId = insertSlide.executeAndReturnKey(parameters);
            slide.setId((int) newId.longValue());

            return slide;
        } catch (DataAccessException ex) {
            logger.error("Failed to create slide", ex);
            throw new DAOException("Failed to create slide", ex);
        }
    }

    private Slide updateBase(Slide slide) throws DAOException, ValidationException {
        logger.debug("Updating slide {}", slide);

        SlideValidator.validate(slide);
        SlideValidator.validateID(slide.getId());

        try {
            jdbcTemplate.update(BASE_UPDATE_STATEMENT, slide.getSlideshowId(), slide.getOrder(),
                    slide.getCaption(), slide.getId());
            logger.debug("Successfully updated slide");
            return slide;
        } catch (DataAccessException ex) {
            logger.error("Failed to update slide", ex);
            throw new DAOException("Failed to update slide", ex);
        }
    }

    private class PhotoSlideMapper implements RowMapper<PhotoSlide> {
        @Override public PhotoSlide mapRow(ResultSet rs, int rowNum) throws SQLException {
            Photo photo;

            try {
                photo = photoDAO.getById(rs.getInt(5));
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            }

            return new PhotoSlide(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), photo);
        }
    }

    private class MapSlideMapper implements RowMapper<MapSlide> {
        @Override public MapSlide mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MapSlide(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4),
                    rs.getDouble(5), rs.getDouble(6), rs.getInt(7));
        }
    }

    private class TitleSlideMapper implements RowMapper<TitleSlide> {
        @Override public TitleSlide mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TitleSlide(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4),
                    rs.getInt(5));
        }
    }
}
