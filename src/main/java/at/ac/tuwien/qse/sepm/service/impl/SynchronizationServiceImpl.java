package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.AsyncPhotoRepository;
import at.ac.tuwien.qse.sepm.dao.repo.Operation;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SynchronizationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SynchronizationServiceImpl implements SynchronizationService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private AsyncPhotoRepository repository;

    @Override public Queue<Operation> getQueue() {
        return repository.getQueue();
    }

    @Override public void subscribeQueue(Consumer<Operation> callback) {
        repository.addListener(new AsyncPhotoRepository.AsyncListener() {
            @Override public void onQueue(AsyncPhotoRepository repository, Operation operation) {
                LOGGER.debug("receiving queue notification with {}", operation);
                callback.accept(operation);
            }
        });
    }

    @Override public void subscribeComplete(Consumer<Operation> callback) {
        repository.addListener(new AsyncPhotoRepository.AsyncListener() {
            @Override public void onComplete(AsyncPhotoRepository repository, Operation operation) {
                LOGGER.debug("receiving complete notification with {}", operation);
                callback.accept(operation);
            }
        });
    }

    @Override public void subscribeError(BiConsumer<Operation, ServiceException> callback) {
        repository.addListener(new AsyncPhotoRepository.AsyncListener() {
            @Override public void onError(AsyncPhotoRepository repository, Operation operation, DAOException error) {
                ServiceException ex = new ServiceException(error);
                LOGGER.debug("receiving error notification with {}", operation);
                callback.accept(operation, ex);
            }
        });
    }
}
