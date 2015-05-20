package at.ac.tuwien.qse.sepm.service.impl;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.DropboxService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
            "Dropbox/info.json"
    );

    ExecutorService executorService = Executors.newFixedThreadPool(1);

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
    public Cancelable uploadPhotos(List<Photo> photos, String path, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler) {
        return null;
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
