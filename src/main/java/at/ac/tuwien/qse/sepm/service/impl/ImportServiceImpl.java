package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.CancelableTask;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ImportServiceImpl implements ImportService {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private PhotoDAO photoDAO;
    @Autowired private ExifService exifService;
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public ImportServiceImpl() {

    }

    public Cancelable importPhotos(List<Photo> photos, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler)  {
        logger.debug("Importing photos");
        AsyncImporter importer = new AsyncImporter(photos, callback, errorHandler);
        executorService.submit(importer);

        return importer;
    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    private class AsyncImporter extends CancelableTask {
        private List<Photo> photos;
        private Consumer<Photo> callback;
        private ErrorHandler<ServiceException> errorHandler;

        public AsyncImporter(List<Photo> photos, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler) {
            super();
            this.photos = photos;
            this.callback = callback;
            this.errorHandler = errorHandler;
        }

        @Override
        protected void execute() {
            for (Photo p: photos) {
                if(!isRunning())
                    return;

                try {
                    exifService.attachDateAndGeoData(p);
                    Photo imported = photoDAO.create(p);
                    //exifService.getTagsFromExif(p);
                    callback.accept(imported);
                } catch (DAOException ex) {
                    logger.error("Failed to import photo", ex);
                    errorHandler.propagate(new ServiceException("Failed to import photo", ex));
                    break;
                } catch (ValidationException ex) {
                    logger.error("Failed to validate photo", ex);
                    errorHandler.propagate(new ServiceException("Failed to validate photo", ex));
                    break;
                } catch (ServiceException ex) {
                    logger.error("Failed to attach date", ex);
                    errorHandler.propagate(new ServiceException("Failed to attach date", ex));
                    break;
                }
            }
        }
    }
}
