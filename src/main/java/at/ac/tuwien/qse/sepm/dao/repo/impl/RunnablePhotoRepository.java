package at.ac.tuwien.qse.sepm.dao.repo.impl;

import org.apache.logging.log4j.Logger;

public abstract class RunnablePhotoRepository
        extends PhotoRepositoryBase
        implements Runnable {

    protected RunnablePhotoRepository(Logger logger) {
        super(logger);
    }

    @Override public void run() {

    }
}
