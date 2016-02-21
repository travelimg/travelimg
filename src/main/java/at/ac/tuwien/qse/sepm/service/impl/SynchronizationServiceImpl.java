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
