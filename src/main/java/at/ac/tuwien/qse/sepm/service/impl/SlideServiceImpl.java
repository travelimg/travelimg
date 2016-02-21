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
