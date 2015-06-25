package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.validators.SlideValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCPhotoSlideDAO extends JDBCDAOBase implements SlideDAO<PhotoSlide> {

    private static final String INSERT_STATEMENT = "INSERT INTO PhotoSlide(id, photo_id) VALUES(?, ?)";
    private static final String READ_ALL_BY_SLIDESHOW_STATEMENT = "SELECT Slide.id, slideshow_id, orderposition, caption, photo_id FROM PhotoSlide JOIN Slide ON PhotoSlide.id = Slide.id WHERE slideshow_id=? ORDER BY orderposition asc;";
    private static final String UPDATE_STATEMENT = "UPDATE PhotoSlide SET photo_id = ?  WHERE id = ?;";

    @Autowired
    private SlideDAO<Slide> slideDAO;
    @Autowired
    private PhotoDAO photoDAO;

    @Override
    public PhotoSlide create(PhotoSlide slide) throws DAOException, ValidationException {
        logger.debug("Creating photo slide {}", slide);

        SlideValidator.validate(slide); // TODO: validate photo_id

        // create base slide
        Slide base = slideDAO.create(slide);
        slide.setId(base.getId());

        try {
            jdbcTemplate.update(INSERT_STATEMENT, slide.getId(), slide.getPhoto().getId());
            return slide;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to create photo slide", ex);
        }
    }

    @Override
    public void delete(PhotoSlide slide) throws DAOException, ValidationException {
        // TODO
    }

    @Override
    public PhotoSlide update(PhotoSlide slide) throws DAOException, ValidationException {

        slideDAO.update(slide);

        // TODO
        return slide;
    }

    @Override
    public List<PhotoSlide> getSlidesForSlideshow(int slideshowId) throws DAOException, ValidationException {
        logger.debug("Retrieving photo slides for slideshow with id {}", slideshowId);

        SlideValidator.validateID(slideshowId);

        try {
            return jdbcTemplate.query(READ_ALL_BY_SLIDESHOW_STATEMENT, new PhotoSlideMapper(), slideshowId);
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to retrieve photo slides for given slideshow", ex);
        }
    }

    private class PhotoSlideMapper implements RowMapper<PhotoSlide> {
        @Override
        public PhotoSlide mapRow(ResultSet rs, int rowNum) throws SQLException {
            Photo photo;

            try {
                photo = photoDAO.getById(rs.getInt(5));
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            }

            return new PhotoSlide(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), photo);
        }
    }
}
