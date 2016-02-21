package at.ac.tuwien.qse.sepm.service;

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
import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;


public interface SlideService {

    /**
     * Create a Slide which contains a Slide
     * @param slide
     * @return
     * @throws ServiceException
     */
    PhotoSlide create(PhotoSlide slide) throws ServiceException;

    /**
     * Create a Slide which conatains a Map
     * @param slide
     * @return
     * @throws ServiceException
     */
    MapSlide create(MapSlide slide) throws ServiceException;

    /**
     * Create a Slide which contains a Title
     * @param slide
     * @return
     * @throws ServiceException
     */
    TitleSlide create(TitleSlide slide) throws ServiceException;

    /**
     * Update a PhotoSlide
     * @param slide
     * @return
     * @throws ServiceException
     */
    PhotoSlide update(PhotoSlide slide) throws ServiceException;

    /**
     * Update a MapSlide
     * @param slide
     * @return
     * @throws ServiceException
     */
    MapSlide update(MapSlide slide) throws ServiceException;

    /**
     * Update a TitleSlide
     * @param slide
     * @return
     * @throws ServiceException
     */
    TitleSlide update(TitleSlide slide) throws ServiceException;

    /**
     * Delete a PhotoSlide
     * @param slide
     * @throws ServiceException
     */
    void delete(PhotoSlide slide) throws ServiceException;

    /**
     * Delete a MapSlide
     * @param slide
     * @throws ServiceException
     */
    void delete(MapSlide slide) throws ServiceException;

    /**
     * Delete a TitleSlide
     * @param slide
     * @throws ServiceException
     */
    void delete(TitleSlide slide) throws ServiceException;
}
