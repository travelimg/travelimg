package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
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

    @Override
    public PhotoSlide create(PhotoSlide slide) throws ServiceException {
        try {
            return slideDAO.create(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to create slide {}", slide, ex);
            throw new ServiceException("Failed to create slide", ex);
        }
    }

    @Override
    public MapSlide create(MapSlide slide) throws ServiceException {
        try {
            return slideDAO.create(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to create slide {}", slide, ex);
            throw new ServiceException("Failed to create slide", ex);
        }
    }

    @Override
    public TitleSlide create(TitleSlide slide) throws ServiceException {
        try {
            return slideDAO.create(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to create slide {}", slide, ex);
            throw new ServiceException("Failed to create slide", ex);
        }
    }

    @Override
    public PhotoSlide update(PhotoSlide slide) throws ServiceException {
        try {
            return slideDAO.update(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to update slide {}", slide, ex);
            throw new ServiceException("Failed to update slide", ex);
        }
    }

    @Override
    public MapSlide update(MapSlide slide) throws ServiceException {
        try {
            return slideDAO.update(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to update slide {}", slide, ex);
            throw new ServiceException("Failed to update slide", ex);
        }
    }

    @Override
    public TitleSlide update(TitleSlide slide) throws ServiceException {
        try {
            return slideDAO.update(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to update slide {}", slide, ex);
            throw new ServiceException("Failed to update slide", ex);
        }
    }

    @Override
    public void delete(PhotoSlide slide) throws ServiceException {
        try {
            slideDAO.delete(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to delete slide {}", slide, ex);
            throw new ServiceException("Failed to delete slide", ex);
        }
    }

    @Override
    public void delete(MapSlide slide) throws ServiceException {
        try {
            slideDAO.delete(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to delete slide {}", slide, ex);
            throw new ServiceException("Failed to delete slide", ex);
        }
    }

    @Override
    public void delete(TitleSlide slide) throws ServiceException {
        try {
            slideDAO.delete(slide);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to delete slide {}", slide, ex);
            throw new ServiceException("Failed to delete slide", ex);
        }
    }
}
