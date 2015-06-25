package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.entities.validators.SlideValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCTitleSlideDAO extends JDBCDAOBase implements SlideDAO<TitleSlide> {

    private static final String INSERT_STATEMENT = "INSERT INTO TitleSlide(id, color) VALUES(?, ?)";
    private static final String READ_ALL_BY_SLIDESHOW_STATEMENT = "SELECT Slide.id, slideshow_id, orderposition, caption, color FROM TitleSlide JOIN Slide ON TitleSlide.id = Slide.id WHERE slideshow_id=? ORDER BY orderposition asc;";
    private static final String UPDATE_STATEMENT = "UPDATE TitleSlide SET color = ?  WHERE id = ?;";

    @Autowired
    private SlideDAO<Slide> slideDAO;

    @Override
    public TitleSlide create(TitleSlide slide) throws DAOException, ValidationException {
        logger.debug("Creating color slide {}", slide);

        SlideValidator.validate(slide); // TODO: validate photo_id

        // create base slide
        Slide base = slideDAO.create(slide);
        slide.setId(base.getId());

        try {
            jdbcTemplate.update(INSERT_STATEMENT, slide.getId(), slide.getColor());
            return slide;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to create photo slide", ex);
        }
    }

    @Override
    public void delete(TitleSlide slide) throws DAOException, ValidationException {
        // TODO
    }

    @Override
    public TitleSlide update(TitleSlide slide) throws DAOException, ValidationException {
        logger.debug("Updating title slide {}", slide);

        slideDAO.update(slide);

        SlideValidator.validateID(slide.getId());

        try {
            jdbcTemplate.update(UPDATE_STATEMENT,
                    slide.getColor(),
                    slide.getId()
            );
            logger.debug("Successfully updated title slide");
            return slide;
        } catch (DataAccessException ex) {
            logger.error("Failed to update title slide", ex);
            throw new DAOException("Failed to update title slide", ex);
        }
    }

    @Override
    public List<TitleSlide> getSlidesForSlideshow(int slideshowId) throws DAOException, ValidationException {
        logger.debug("Retrieving title slides for slideshow with id {}", slideshowId);

        SlideValidator.validateID(slideshowId);

        try {
            return jdbcTemplate.query(READ_ALL_BY_SLIDESHOW_STATEMENT, new TitleSlideMapper(), slideshowId);
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to retrieve title slides for given slideshow", ex);
        }
    }

    private class TitleSlideMapper implements RowMapper<TitleSlide> {
        @Override
        public TitleSlide mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TitleSlide(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getInt(5));
        }
    }

}