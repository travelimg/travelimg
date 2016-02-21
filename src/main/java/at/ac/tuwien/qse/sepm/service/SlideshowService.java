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

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;

import java.util.List;


public interface SlideshowService {

    /**
     * Create a Slideshow in the data store.
     *
     * @param slideshow  which to create; must not be null; must not already have an id
     * @return the created slideshow
     * @throws ServiceException If the Slideshow can not be created or the data store fails to
     *      create a record.
     */
    Slideshow create(Slideshow slideshow) throws ServiceException;

    /**
     * Update an existing slideshow.
     *
     * @param slideshow The slideshow to be updated.
     * @return The newly updated slideshow.
     * @throws ServiceException If the update fails.
     */
    Slideshow update(Slideshow slideshow) throws ServiceException;

    /**
     * Delete an existing Slideshow.
     *
     * @param slideshow Specifies which slideshow to delete by providing the id;
     *            must not be null;
     *            <tt>slideshow.id</tt> must not be null;
     * @throws ServiceException If the Slideshwo can not be deleted or the data store fails to
     *     delete the record.
     */
    void delete(Slideshow slideshow) throws ServiceException;

    /**
     * Return a list of all existing slideshows.
     *
     * @return the list of all available slideshows
     * @throws ServiceException if retrieval failed
     */
    List<Slideshow> getAllSlideshows() throws ServiceException;

    /**
     * Add a Photo to a Slideshow
     * @param photos
     * @param slideshow
     * @return
     * @throws ServiceException
     */
    List<Slide> addPhotosToSlideshow(List<Photo> photos, Slideshow slideshow) throws ServiceException;
}
