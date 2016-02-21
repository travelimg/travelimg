package at.ac.tuwien.qse.sepm.service.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
