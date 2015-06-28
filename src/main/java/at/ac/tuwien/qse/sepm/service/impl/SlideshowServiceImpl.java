package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.SlideDAO;
import at.ac.tuwien.qse.sepm.dao.SlideshowDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.ServiceException;

import at.ac.tuwien.qse.sepm.service.SlideshowService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mb on 08.06.15.
 */
public class SlideshowServiceImpl implements SlideshowService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private SlideshowDAO slideshowDAO;

    @Autowired
    private SlideDAO slideDAO;

    @Override
    public Slideshow create(Slideshow slideshow) throws ServiceException {

        try {
            return slideshowDAO.create(slideshow);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to create slideshow", ex);
            throw new ServiceException("Failed to create slideshow", ex);
        }
    }

    @Override
    public Slideshow update(Slideshow slideshow) throws ServiceException {
        try {
            return slideshowDAO.update(slideshow);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to update slideshow", ex);
            throw new ServiceException("Failed to update slideshow", ex);
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

    @Override
    public List<Slide> addPhotosToSlideshow(List<Photo> photos, Slideshow slideshow) throws ServiceException {
        List<Slide> slides = new ArrayList<>();
        int order = slideshow.getAllSlides().size() + 1;

        try {
            for (Photo photo : photos) {
                PhotoSlide slide = new PhotoSlide(-1, slideshow.getId(), order, "", photo);
                slide = slideDAO.create(slide);

                slides.add(slide);
                slideshow.getPhotoSlides().add(slide);
                order++;
            }
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to create slide", ex);
            throw new ServiceException("Failed to create slide", ex);
        }

        if(slides.size()==0){
            throw new ServiceException("List is empty");
        }else {
            return slides;
        }
    }
}