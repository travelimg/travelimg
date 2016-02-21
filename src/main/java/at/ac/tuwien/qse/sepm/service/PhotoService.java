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

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Service for manipulating and organizing photos.
 */
public interface PhotoService {

    /**
     * delete the delivered List of Photos
     *
     * @param photos the list of photos
     * @throws ServiceException
     */
    void deletePhotos(Collection<Photo> photos) throws ServiceException;

    /**
     * @return the list of all available photos
     * @throws ServiceException
     */
    List<Photo> getAllPhotos() throws ServiceException;

    /**
     * Get all photos that match the specified filter.
     *
     * @param filter filter the photos are tested against
     * @return list of all available photos that match the filter
     */
    List<Photo> getAllPhotos(Predicate<Photo> filter) throws ServiceException;

    /**
     * Persist the edits made to a photo.
     *
     * @param photo photo for which the changes should be stored
     * @throws ServiceException failed to perform operation
     */
    void editPhoto(Photo photo) throws ServiceException;

    /**
     * Listen for photos that have been newly added.
     *
     * @param callback callback that receives the added photos
     */
    void subscribeCreate(Consumer<Photo> callback);

    /**
     * Listen for photos that have been updated.
     *
     * @param callback callback that receives the updated photos
     */
    void subscribeUpdate(Consumer<Photo> callback);

    /**
     * Listen for photos that have been deleted.
     *
     * @param callback callback that receives the deleted photos
     */
    void subscribeDelete(Consumer<Path> callback);

    /**
     * Setup the repository to watch the currently managed image directories and synchronize with changes
     * from the file system.
     *
     * Should only be called once on startup.
     */
    void initializeRepository();
}
