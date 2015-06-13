package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.Service;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by mb on 08.06.15.
 */
public class SlideServiceImpl implements SlideService{

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private SlideDAO slideDAO;

    @Override public void create(Slide slide) throws ServiceException {
        try {
            slideDAO.create(slide);

        } catch (DAOException | ValidationException ex) {
            throw new ServiceException("Failed to create Slide", ex);
        }
    }

    @Override public void delete(Slide slide) throws ServiceException {

        try {
            slideDAO.delete(slide);
        } catch (DAOException | ValidationException ex) {
            throw new ServiceException("Failed to delete slide", ex);
        }
    }

    @Override
    public Slide update(Slide slide) throws ServiceException {
        LOGGER.debug("Updating slide {}", slide);

        try {
            return slideDAO.update(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to update slide", ex);
            throw new ServiceException("Failed to update slide", ex);
        }
    }

    @Override public List<Slide> readAll() throws ServiceException {
        LOGGER.debug("Retrieving all slides");
        try {
            return slideDAO.readAll();
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
}
