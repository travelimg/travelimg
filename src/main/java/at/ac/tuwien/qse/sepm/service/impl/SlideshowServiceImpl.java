package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.SlideshowDAO;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.ServiceException;

import at.ac.tuwien.qse.sepm.service.SlideshowService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by mb on 08.06.15.
 */
public class SlideshowServiceImpl implements SlideshowService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private SlideshowDAO slideshowDAO;

    @Override
    public void create(Slideshow slideshow) throws ServiceException {

        try {
            slideshowDAO.create(slideshow);
        } catch (DAOException | ValidationException ex) {
            throw new ServiceException("Failed to create slideshow",ex);
        }


    }

    @Override
    public void delete(Slideshow slideshow) throws ServiceException {

        try {
            slideshowDAO.delete(slideshow);
        } catch (DAOException | ValidationException ex) {
            throw new ServiceException("Failed to delete slideshow",ex);
        }
    }

    @Override
    public List<Slideshow> getAllSlideshows() throws ServiceException {

        try {
            return slideshowDAO.readAll();
        } catch (DAOException ex) {
            throw new ServiceException("Failed to return a List of slideshows",ex);
        }
    }

}
