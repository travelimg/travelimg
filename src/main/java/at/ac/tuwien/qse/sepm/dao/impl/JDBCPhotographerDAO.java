package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotographerDAO;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.PhotographerValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class JDBCPhotographerDAO extends JDBCDAOBase implements PhotographerDAO {

    private static final Logger logger = LogManager.getLogger();

    private static final String readStatement = "SELECT* FROM Photographer WHERE ID=?;";
    private static final String readAllStatement = "SELECT* FROM Photographer;";

    private SimpleJdbcInsert insertPhotographer;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertPhotographer = new SimpleJdbcInsert(dataSource)
                .withTableName("Photographer")
                .usingGeneratedKeyColumns("id");
    }

    public Photographer create(Photographer p) throws DAOException, ValidationException {
        logger.debug("Creating photographer {}", p);
        PhotographerValidator.validate(p);

        try {
            Map<String, Object> parameters = new HashMap<String, Object>(1);
            parameters.put("name", p.getName());
            Number newId = insertPhotographer.executeAndReturnKey(parameters);
            p.setId((int)newId.longValue());
            return p;
        } catch (DataAccessException e) {
            throw new DAOException("Failed to create photographer", e);
        }
    }

    public Photographer read(Photographer p) throws DAOException {
        try(PreparedStatement stmt = getConnection().prepareStatement(readStatement)) {

            stmt.setInt(1,p.getId());
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                throw new DAOException("Photographer not found");
            }
            p.setName(rs.getString(2));
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return p;
    }

    public List<Photographer> readAll() throws DAOException {
        try {
            return jdbcTemplate.query(readAllStatement, new RowMapper<Photographer>() {
                @Override
                public Photographer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Photographer(rs.getInt(1), rs.getString(2));
                }
            });
        }
        catch(DataAccessException e){
            throw new DAOException("Failed to read all photographers", e);
        }
    }

}
