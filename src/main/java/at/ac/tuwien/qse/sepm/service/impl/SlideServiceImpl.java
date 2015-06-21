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
    private SlideDAO<PhotoSlide> photoSlideDAO;
    @Autowired
    private SlideDAO<MapSlide> mapSlideDAO;
    @Autowired
    private SlideDAO<TitleSlide> titleSlideDAO;


    @Override public Slide create(Slide slide) throws ServiceException {

        try {
            if (slide instanceof PhotoSlide) {
                return photoSlideDAO.create((PhotoSlide)slide);
            } else if (slide instanceof MapSlide) {
                return mapSlideDAO.create((MapSlide)slide);
            } else if (slide instanceof TitleSlide) {
                return titleSlideDAO.create((TitleSlide)slide);
            }
            return null;
        } catch (DAOException | ValidationException ex) {
            throw new ServiceException("Failed to create Slide", ex);
        }
    }

    @Override public void delete(Slide slide) throws ServiceException {

        try {
            if (slide instanceof PhotoSlide) {
                photoSlideDAO.delete((PhotoSlide)slide);
            } else if (slide instanceof MapSlide) {
                mapSlideDAO.delete((MapSlide)slide);
            } else if (slide instanceof TitleSlide) {
                titleSlideDAO.delete((TitleSlide)slide);
            }
        } catch (DAOException | ValidationException ex) {
            throw new ServiceException("Failed to delete slide", ex);
        }
    }

    @Override
    public Slide update(Slide slide) throws ServiceException {
        LOGGER.debug("Updating slide {}", slide);

        try {
            if (slide instanceof PhotoSlide) {
                return photoSlideDAO.update((PhotoSlide) slide);
            } else if (slide instanceof MapSlide) {
                return mapSlideDAO.update((MapSlide) slide);
            } else if (slide instanceof TitleSlide) {
                return titleSlideDAO.update((TitleSlide) slide);
            }

            return null;
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to update slide", ex);
            throw new ServiceException("Failed to update slide", ex);
        }
    }
}
