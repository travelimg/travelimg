package at.ac.tuwien.qse.sepm.gui.controller.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Refresher {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private boolean dirty = true;

    protected void start(int period, TimeUnit unit) {
        if (unit == null) throw new IllegalArgumentException();
        executor.scheduleAtFixedRate(() -> {
            if (!dirty) return;
            dirty = false;
            refresh();
        }, 0, period, unit);
    }

    protected void markDirty() {
        dirty = true;
    }

    protected abstract void refresh();

    public void stop() {
        executor.shutdown();
    }
}
