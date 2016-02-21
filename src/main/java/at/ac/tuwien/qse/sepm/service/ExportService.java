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
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service for uploading photos to a destination in the file system.
 */
public interface ExportService {

    /**
     * Fetch the dropbox base directory.
     *
     * @return The base directory of the users dropbox or null if it can't be found.
     */
    String getDropboxFolder();

    /**
     * Export a given set of photos to the specified location.
     * <p>
     * The exact folder is specified by the user and the upload happens in the background.
     *
     * @param photos       The photos to upload.
     * @param destination  The path where to the photos are exported.
     * @param callback     Callback which is called after a photo is finished exporting.
     * @param errorHandler Handler for occuring exceptions.
     * @return A cancelable task for aborting the export if desired.
     */
    Cancelable exportPhotos(Collection<Photo> photos, String destination, Consumer<Photo> callback,
            ErrorHandler<ServiceException> errorHandler);
}
