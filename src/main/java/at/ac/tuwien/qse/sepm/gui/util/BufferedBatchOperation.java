package at.ac.tuwien.qse.sepm.gui.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A buffer for an operation which can be executed as a batch.
 * <p>
 * Elements for the operation can be added and the operation will be executed after a fixed time
 * interval as a batch.
 */
public class BufferedBatchOperation<T> {

    private final ScheduledExecutorService scheduler;
    private final List<T> buffer = new LinkedList<>();
    private ScheduledFuture<?> future = null;
    private int delay = 2;
    private Consumer<List<T>> callback;

    public BufferedBatchOperation(Consumer<List<T>> callback, ScheduledExecutorService scheduler) {
        this.callback = callback;
        this.scheduler = scheduler;
    }

    /**
     * Add an element to the batch.
     *
     * @param element The element to be added.
     */
    public synchronized void add(T element) {
        buffer.add(element);

        if (future == null) {
            future = scheduler.schedule(this::commit, delay, TimeUnit.SECONDS);
        }
    }

    private synchronized void commit() {
        if (buffer.isEmpty()) {
            future = null;
            return;
        }

        callback.accept(new ArrayList<>(buffer));
        buffer.clear();

        future = scheduler.schedule(this::commit, delay, TimeUnit.SECONDS);
    }

}
