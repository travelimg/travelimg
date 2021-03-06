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

import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import com.flickr4java.flickr.photos.Photo;

import java.util.List;
import java.util.function.Consumer;

/**
 * Service for accessing and downloading photos from flickr.
 */
public interface FlickrService {

    /**
     * Downloads (if possible) 9 new photos(in medium size) every time it is called. This is a non-blocking operation
     *
     * @param tags         a list of tags used as keywords inorder to perform the search
     * @param latitude     used to find photos near to it
     * @param longitude    used to find photos near to ii
     * @param useGeoData   if true, geodata(latitude and longitude) will be used for searching
     * @param callback     used to notify the GUI after a new photo has been downloaded
     * @param progressCallback used to notify the GUI about the download progress
     * @param errorHandler handler for occurring exceptions
     * @return a Cancelable object, that can be used to interrupt the download
     */
    Cancelable searchPhotos(String tags[], double latitude, double longitude, boolean useGeoData,
            Consumer<Photo> callback, Consumer<Double> progressCallback,
            ErrorHandler<ServiceException> errorHandler);

    /**
     * Downloads photos from flickr in original size
     * @param photos the photos, should contain the url as path, geodata and date
     * @param callback used to notify the GUI after a new photo has been downloaded
     * @param progressCallback used to notify the GUI about the download progress
     * @param errorHandler handler for occurring exceptions
     * @return a Cancelable object, that can be used to interrupt the download
     */
    Cancelable downloadPhotos(List<Photo> photos, Consumer<Photo> callback, Consumer<Double> progressCallback,
            ErrorHandler<ServiceException> errorHandler);

    /**
     * After calling this method, the service will be ready to load photos again from the beginning.
     */
    void reset();

    /**
     * Cleanup used resources.
     */
    void close();
}
