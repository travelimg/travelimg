package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotographerDAO;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.Service;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PhotographerServiceImpl implements PhotographerService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private PhotographerDAO photographerDAO;

    @Override
    public Photographer create(Photographer photographer) throws ServiceException {
        try {
            return photographerDAO.create(photographer);
        } catch (DAOException | ValidationException ex) {
            throw new ServiceException("Failed to create photographer", ex);
        }
    }

    @Override
    public void update(Photographer photographer) throws ServiceException {
        try {
            photographerDAO.update(photographer);
        } catch (DAOException | ValidationException ex) {
            throw new ServiceException("Failed to update photographer", ex);
        }
    }

    @Override
    public List<Photographer> readAll() throws ServiceException {
        LOGGER.debug("read all photographers");

        try {
            return photographerDAO.readAll();
        } catch(DAOException ex) {
            LOGGER.debug("Failed to retrieve all photographers", ex);
            throw new ServiceException("Failed to retrieve all photographers", ex);
        }
    }
}
