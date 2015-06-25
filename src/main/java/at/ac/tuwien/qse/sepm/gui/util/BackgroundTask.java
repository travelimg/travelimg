package at.ac.tuwien.qse.sepm.gui.util;

import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.application.Platform;

import java.util.concurrent.Callable;

public abstract class BackgroundTask<T> implements Runnable {

    @Override
    public void run() {
        try {
            T result = compute();
            Platform.runLater(() -> onFinished(result));
        } catch (ComputeException ex) {
            // compute failed -> ignore
        }
    }

    public abstract T compute() throws ComputeException;

    public void onFinished(T result) {

    }

    public static class ComputeException extends Exception {

    }
}
