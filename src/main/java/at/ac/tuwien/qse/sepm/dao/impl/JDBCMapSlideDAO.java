package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.validators.SlideValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class JDBCMapSlideDAO extends JDBCDAOBase implements SlideDAO<MapSlide> {

    private static final String INSERT_STATEMENT = "INSERT INTO MapSlide(id, latitude, longitude) VALUES(?, ?, ?)";
    private static final String READ_ALL_BY_SLIDESHOW_STATEMENT = "SELECT Slide.id, slideshow_id, orderposition, caption, latitude, longitude FROM MapSlide JOIN Slide ON MapSlide.id = Slide.id WHERE slideshow_id=? ORDER BY orderposition asc;";
    private static final String UPDATE_STATEMENT = "UPDATE MapSlide SET latitude = ?, longitude = ?  WHERE id = ?;";

    @Autowired
    private SlideDAO<Slide> slideDAO;

    @Override
    public MapSlide create(MapSlide slide) throws DAOException, ValidationException {
        logger.debug("Creating photo slide {}", slide);

        SlideValidator.validate(slide); // TODO: validate photo_id

        // create base slide
        Slide base = slideDAO.create(slide);
        slide.setId(base.getId());

        try {
            jdbcTemplate.update(INSERT_STATEMENT, slide.getId(), slide.getLatitude(), slide.getLongitude());
            return slide;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to create map slide", ex);
        }
    }

    @Override
    public void delete(MapSlide slide) throws DAOException, ValidationException {
        // TODO
    }

    @Override
    public MapSlide update(MapSlide slide) throws DAOException, ValidationException {

        slideDAO.update(slide);

        // TODO
        return slide;
    }

    @Override
    public List<MapSlide> getSlidesForSlideshow(int slideshowId) throws DAOException, ValidationException {
        logger.debug("Retrieving map slides for slideshow with id {}", slideshowId);

        SlideValidator.validateID(slideshowId);

        try {
            return jdbcTemplate.query(READ_ALL_BY_SLIDESHOW_STATEMENT, new MapSlideMapper(), slideshowId);
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to retrieve map slides for given slideshow", ex);
        }
    }

    private class MapSlideMapper implements RowMapper<MapSlide> {
        @Override
        public MapSlide mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MapSlide(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getDouble(5), rs.getDouble(6));
        }
    }
}
