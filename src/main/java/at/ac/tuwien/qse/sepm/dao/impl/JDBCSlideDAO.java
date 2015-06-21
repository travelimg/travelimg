package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.dao.SlideshowDAO;
import at.ac.tuwien.qse.sepm.entities.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class JDBCSlideDAO extends JDBCDAOBase implements SlideDAO<Slide> {

    private static final String UPDATE_STATEMENT = "UPDATE SLIDE SET slideshow_id = ?, orderposition = ?, caption = ? WHERE id = ?;";

    private SimpleJdbcInsert insertSlide;

    @Autowired
    private SlideDAO<PhotoSlide> photoSlideDAO;
    @Autowired
    private SlideDAO<MapSlide> mapSlideDAO;
    @Autowired
    private SlideDAO<TitleSlide> titleSlideDAO;


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

        // TODO: validate slide

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

    @Override public void delete(Slide slide) throws DAOException, ValidationException {

    }

    @Override
    public Slide update(Slide slide) throws DAOException, ValidationException {
        logger.debug("Updating slide {}", slide);

        SlideValidator.validate(slide);
        SlideValidator.validateID(slide.getId());

        try {
            jdbcTemplate.update(UPDATE_STATEMENT,
                    slide.getSlideshowId(),
                    slide.getOrder(),
                    slide.getCaption(),
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

        List<Slide> slides = new ArrayList<>();
        slides.addAll(photoSlideDAO.getSlidesForSlideshow(slideshowId));
        slides.addAll(mapSlideDAO.getSlidesForSlideshow(slideshowId));
        slides.addAll(titleSlideDAO.getSlidesForSlideshow(slideshowId));

        return slides.stream()
                .sorted((s1, s2) -> s1.getOrder().compareTo(s2.getOrder()))
                .collect(Collectors.toList());
    }
}
