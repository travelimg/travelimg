package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ImportServiceImpl implements ImportService {

    private static final Logger logger = LogManager.getLogger();

    private PhotoDAO photoDAO;
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public ImportServiceImpl(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }

    public void importPhotos(List<Photo> photos, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler)  {
        AsyncImporter importer = new AsyncImporter(photos, callback, errorHandler);
        executorService.submit(importer);
    }

    public void addPhotographerToPhotos(List<Photo> photos, Photographer photographer) throws ServiceException {

    }

    public void editPhotographerForPhotos(List<Photo> photos, Photographer photographer) throws ServiceException {

    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    private class AsyncImporter implements Runnable {
        private List<Photo> photos;
        private Consumer<Photo> callback;
        private ErrorHandler<ServiceException> errorHandler;

        public AsyncImporter(List<Photo> photos, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler) {
            this.photos = photos;
            this.callback = callback;
            this.errorHandler = errorHandler;
        }

        @Override
        public void run() {
            for(Photo p: photos) {
                try {
                    Photo imported = photoDAO.create(p);
                    callback.accept(imported);
                } catch(DAOException e) {
                    errorHandler.propagate(new ServiceException("Failed to import photo", e));
                    return;
                } catch(ValidationException e) {
                    errorHandler.propagate(new ServiceException("Failed to validate photo", e));
                    return;
                }
            }
        }
    }
}
