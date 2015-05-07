package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

public abstract class ServiceExceptionHandler implements ErrorHandler<ServiceException> {

    public ServiceExceptionHandler() {

    }

    @Override
    public abstract void handle(ServiceException exception);
}
