package at.ac.tuwien.qse.sepm.service.impl;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.DropboxService;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class DropboxServiceImpl implements DropboxService {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Path DROPBOX_INFO_PATH_POSIX = Paths.get(
            System.getProperty("user.home"),
            ".dropbox/info.json"
    );
    private static final Path DROPBOX_INFO_PATH_WIN = Paths.get(
            System.getProperty("user.home"),
            "AppData/Roaming/Dropbox/info.json"
    );

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Autowired
    private IOHandler ioHandler;

    @Override
    public String getDropboxFolder() throws ServiceException {
        Path dropboxInfoPath;

        if (Files.exists(DROPBOX_INFO_PATH_POSIX)) {
            dropboxInfoPath = DROPBOX_INFO_PATH_POSIX;
        } else if (Files.exists(DROPBOX_INFO_PATH_WIN)) {
            dropboxInfoPath = DROPBOX_INFO_PATH_WIN;
        } else {
            LOGGER.error("Could not find dropbox configuration file");
            throw new ServiceException("Could not find dropbox configuration file");
        }

        String info;
        try {
            info = new String(Files.readAllBytes(dropboxInfoPath));
        } catch (IOException ex) {
            LOGGER.error("Failed to read dropbox configuration file", ex);
            throw new ServiceException("Failed to read dropbox configuration file", ex);
        }

        try {
            JSONObject obj = new JSONObject(info);
            return obj.getJSONObject("personal").getString("path");
        } catch (JSONException ex) {
            LOGGER.error("Failed to retrieve dropbox folder location", ex);
            return "";
        }
    }

    @Override
    public Cancelable uploadPhotos(List<Photo> photos, String destination, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler) {
        AsyncExporter exporter = new AsyncExporter(photos, destination, callback, errorHandler);
        executorService.submit(exporter);

        return exporter;
    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    private class AsyncExporter extends CancelableTask {
        private List<Photo> photos;
        private String destination;
        private Consumer<Photo> callback;
        private ErrorHandler<ServiceException> errorHandler;

        public AsyncExporter(List<Photo> photos, String destination, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler) {
            super();
            this.photos = photos;
            this.destination = destination;
            this.callback = callback;
            this.errorHandler = errorHandler;
        }

        @Override
        protected void execute() {

            Path dest;
            // get the target path by combining dropbox root folder and the destination inside the dropbox folder
            try {
                dest = Paths.get(getDropboxFolder(), destination);
                if (!Files.exists(dest)) {
                    throw new ServiceException("Can't upload to dropboxfolder which does not exist: " + dest.toString());
                }
            } catch (ServiceException ex) {
                LOGGER.error("Failed to upload photos to dropbox", ex);
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
                    LOGGER.error("Failed to export photo to dropbox", ex);
                    errorHandler.propagate(new ServiceException("Failed to export photo to dropbox", ex));
                }
            }
        }
    }
}
