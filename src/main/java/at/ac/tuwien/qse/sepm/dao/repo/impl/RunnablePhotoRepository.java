package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PhotoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.plugin.dom.exception.InvalidStateException;

public abstract class RunnablePhotoRepository implements PhotoRepository, Runnable {

    private static final Logger LOGGER = LogManager.getLogger();

    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    @Override public void run() {
        if (isRunning()) {
            LOGGER.error("repository is already running");
            throw new InvalidStateException("Repository is already running.");
        }
        running = true;
    }

    public void stop() {
        if (!isRunning()) {
            LOGGER.error("repository is not running");
            throw new InvalidStateException("Repository is not running.");
        }
        running = false;
    }
}
