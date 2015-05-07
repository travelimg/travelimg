package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotographerDAO;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.PhotographerValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCPhotographerDAO extends JDBCDAOBase implements PhotographerDAO {

    private static final Logger logger = LogManager.getLogger();

    private static final String insertStatement = "INSERT INTO Photographer(name) VALUES (?);";
    private static final String readStatement = "SELECT* FROM Photographer WHERE ID=?;";
    private static final String readAllStatement = "SELECT* FROM Photographer;";

    public Photographer create(Photographer p) throws DAOException, ValidationException {
        logger.debug("Creating photographer {}", p);
        PhotographerValidator.validate(p);

        try(PreparedStatement stmt = getConnection().prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getName());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            p.setId(rs.getInt(1));
            logger.debug("Created photographer {}", p);
            return p;
        } catch (SQLException e) {
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
        List<Photographer> photographers = new ArrayList<Photographer>();
        try(PreparedStatement stmt = getConnection().prepareStatement(readAllStatement)) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                photographers.add(new Photographer(
                        rs.getInt(1),
                        rs.getString(2)
                ));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }

        return photographers;
    }
}
