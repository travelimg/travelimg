package at.ac.tuwien.qse.sepm.dao;

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

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface SlideDAO{

    /**
     * Create a Slide which contains a Photo
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    PhotoSlide create(PhotoSlide slide) throws DAOException, ValidationException;

    /**
     * Create a Slide which contains a Map
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    MapSlide create(MapSlide slide) throws DAOException, ValidationException;

    /**
     * Create a Slide which contains a Title
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    TitleSlide create(TitleSlide slide) throws DAOException, ValidationException;

    /**
     * Update a PhotoSlide
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    PhotoSlide update(PhotoSlide slide) throws DAOException, ValidationException;

    /**
     * Update a MapSlide
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    MapSlide update(MapSlide slide) throws DAOException, ValidationException;

    /**
     * Update a TitleSide
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    TitleSlide update(TitleSlide slide) throws DAOException, ValidationException;

    /**
     * Delete a PhotoSlide
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */
    void delete(PhotoSlide slide) throws DAOException, ValidationException;

    /**
     * Delete a MapSlide
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */
    void delete(MapSlide slide) throws DAOException, ValidationException;

    /**
     * Delete a Title Slide
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */
    void delete(TitleSlide slide) throws DAOException, ValidationException;

    /**
     * Delete all slides which contain the given photo.
     *
     * @param photo The photo for which slides should be deleted.
     * @throws DAOException If the slides can not be deleted.
     */
    void deleteAllSlidesWithPhoto(Photo photo) throws DAOException;

    /**
     * Get all PhotoSlides
     * @param slideshowId
     * @return
     * @throws DAOException
     */
    List<PhotoSlide> getPhotoSlidesForSlideshow(int slideshowId) throws DAOException;

    /**
     * Get all MapSlides
     * @param slideshowId
     * @return
     * @throws DAOException
     */
    List<MapSlide> getMapSlidesForSlideshow(int slideshowId) throws DAOException;

    /**
     * Get all TitleSlides
     * @param slideshowId
     * @return
     * @throws DAOException
     */
    List<TitleSlide> getTitleSlidesForSlideshow(int slideshowId) throws DAOException;
}
