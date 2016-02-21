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

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ExportService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.CancelableTask;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class ExportServiceImpl implements ExportService {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Path DROPBOX_INFO_PATH_POSIX = Paths.get(
            System.getProperty("user.home"),
            ".dropbox/info.json"
    );
    private static final Path DROPBOX_INFO_PATH_WIN = Paths.get(
            System.getProperty("user.home"),
            "AppData/Roaming/Dropbox/info.json"
    );

    @Autowired
    private ExecutorService executor;

    @Autowired
    private IOHandler ioHandler;

    @Override
    public String getDropboxFolder() {
        Path dropboxInfoPath = getDropboxInfoPath();

        LOGGER.debug("dropbox info path is {}", dropboxInfoPath);

        if (dropboxInfoPath == null) {
            return null;
        }

        String info;
        try {
            LOGGER.debug("reading dropbox info file");
            info = new String(Files.readAllBytes(dropboxInfoPath));
            LOGGER.debug("read dropbox info file");
        } catch (IOException ex) {
            LOGGER.error("Failed to read dropbox configuration file", ex);
            return null;
        }

        try {
            LOGGER.debug("parsing dropbox info file to JSON");
            JSONObject obj = new JSONObject(info);
            String dropboxPath = obj.getJSONObject("personal").getString("path");
            LOGGER.debug("found dropbox path as {}", dropboxPath);
            return dropboxPath;
        } catch (JSONException ex) {
            LOGGER.error("Failed to retrieve dropbox folder location", ex);
            return null;
        }
    }

    @Override
    public Cancelable exportPhotos(Collection<Photo> photos, String destination,
            Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler) {
        LOGGER.debug("exporting photos to {}", destination);
        AsyncExporter exporter = new AsyncExporter(photos, destination, callback, errorHandler);
        executor.submit(exporter);

        return exporter;
    }

    /**
     * Return the path of the dropbox info file or null if it can't be found.
     *
     * @return Path ot dropbox info or null.
     */
    public Path getDropboxInfoPath() {
        if (Files.exists(DROPBOX_INFO_PATH_POSIX)) {
            return DROPBOX_INFO_PATH_POSIX;
        } else if (Files.exists(DROPBOX_INFO_PATH_WIN)) {
            return DROPBOX_INFO_PATH_WIN;
        } else {
            LOGGER.error("Could not find dropbox configuration file");
            return null;
        }
    }

    private class AsyncExporter extends CancelableTask {
        private Collection<Photo> photos;
        private String destination;
        private Consumer<Photo> callback;
        private ErrorHandler<ServiceException> errorHandler;

        public AsyncExporter(Collection<Photo> photos, String destination, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler) {
            super();
            this.photos = photos;
            this.destination = destination;
            this.callback = callback;
            this.errorHandler = errorHandler;
        }

        @Override
        protected void execute() {
            LOGGER.debug("executing async exporter");
            Path dest;

            try {
                dest = Paths.get(destination);
                LOGGER.debug("destination is {}", destination);
                if (!Files.exists(dest)) {
                    LOGGER.debug("destination does not exist at {}", dest);
                    throw new ServiceException("Can't export to folder which does not exist: " + dest.toString());
                }
            } catch (ServiceException ex) {
                LOGGER.error("Failed to export photos to folder", ex);
                errorHandler.propagate(ex);
                return;
            }

            for (Photo photo : photos) {
                if (!isRunning())
                    return;

                try {
                    Path source = Paths.get(photo.getPath());
                    String fileName = source.getFileName().toString();
                    Path target = Paths.get(dest.toString(), fileName);

                    ioHandler.copyFromTo(source, target);
                    callback.accept(photo);

                } catch (Exception ex) {
                    LOGGER.error("Failed to export photo to folder", ex);
                    errorHandler.propagate(new ServiceException("Failed to export photo to folder", ex));
                }
            }
        }
    }
}
