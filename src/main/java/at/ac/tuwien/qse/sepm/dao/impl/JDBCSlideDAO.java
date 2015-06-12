package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.entities.Slide;
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

/**
 * Created by mb on 08.06.15.
 */
public class JDBCSlideDAO extends JDBCDAOBase implements SlideDAO {

    private static final String READ_ALL_STATEMENT = "SELECT id, photo_id, slideshow_id, orderposition FROM SLIDE;";

    private SimpleJdbcInsert insertSlide;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertSlide = new SimpleJdbcInsert(dataSource)
                .withTableName("Slide")
                .usingGeneratedKeyColumns("id");
    }


    @Override public void create(Slide slide) throws DAOException, ValidationException {
        logger.debug("Creating slide {}",slide);

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("id", slide.getId());
            parameters.put("photo_id", slide.getPhoto_id());
            parameters.put("slideshow_id",slide.getSlideshow_id());
            parameters.put("orderposition",slide.getOrder());
            Number newId = insertSlide.executeAndReturnKey(parameters);
            slide.setId((int) newId.longValue());
        } catch (DataAccessException ex) {
            logger.error("Failed to create slide", ex);
            throw new DAOException("Failed to create slide", ex);
        }
    }

    @Override public void delete(Slide slide) throws DAOException, ValidationException {

    }

    @Override public List<Slide> readAll() throws DAOException {
        logger.debug("retrieving all slides");

        try {
            return jdbcTemplate.query(READ_ALL_STATEMENT, new RowMapper<Slide>() {
                @Override public Slide mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Slide(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4)
                    );
                }
            });
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all slides", e);
        }
    }
}
