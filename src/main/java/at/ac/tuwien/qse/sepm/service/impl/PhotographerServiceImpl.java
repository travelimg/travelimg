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
import at.ac.tuwien.qse.sepm.dao.PhotographerDAO;
import at.ac.tuwien.qse.sepm.dao.EntityWatcher;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

public class PhotographerServiceImpl implements PhotographerService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private PhotographerDAO photographerDAO;
    private Consumer<Photographer> refreshPhotographers;

    @Autowired private void setEntityWatcher(EntityWatcher<Photographer> watcher) {
        watcher.subscribeAdded(this::photographerAdded);
    }

    @Override
    public Photographer create(Photographer photographer) throws ServiceException {
        try {
            photographer = photographerDAO.create(photographer);
            photographerAdded(photographer);
            return photographer;
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
        } catch (DAOException ex) {
            LOGGER.debug("Failed to retrieve all photographers", ex);
            throw new ServiceException("Failed to retrieve all photographers", ex);
        }
    }

    private void photographerAdded(Photographer photographer) {
        if (refreshPhotographers != null) {
            refreshPhotographers.accept(photographer);
        }
    }

    @Override public void subscribeChanged(Consumer<Photographer> callback) {
        this.refreshPhotographers = callback;
    }
}
