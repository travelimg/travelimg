package at.ac.tuwien.qse.sepm.gui.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BufferedBatchOperation<T> {

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future = null;

    private final List<T> buffer = new LinkedList<>();
    private Consumer<List<T>> callback;

    public BufferedBatchOperation(Consumer<List<T>> callback, ScheduledExecutorService scheduler) {
        this.callback = callback;
        this.scheduler = scheduler;
    }

    public void add(T element) {
        synchronized (buffer) {
            buffer.add(element);

            if (future == null) {
                future = scheduler.schedule(this::commit, 2, TimeUnit.SECONDS);
            }
        }
    }

    private void commit() {
        synchronized (buffer) {
            if (buffer.isEmpty()) {
                future = null;
                return;
            }

            callback.accept(new ArrayList<>(buffer));
            buffer.clear();

            future = scheduler.schedule(this::commit, 2, TimeUnit.SECONDS);
        }
    }


}
