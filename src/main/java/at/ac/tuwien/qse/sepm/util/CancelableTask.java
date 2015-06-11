package at.ac.tuwien.qse.sepm.util;


import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CancelableTask implements Runnable, Cancelable {

    private AtomicBoolean finished = new AtomicBoolean(false);
    private AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Return the current execution status of the task.
     *
     * @return true if the task is currently executing else false.
     */
    protected boolean isRunning() {
        return running.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        running.set(true);
        execute();
        running.set(false);
        finished.set(true);
    }

    /**
     * Cancel a running task.
     * <p>
     * The processing will stop as soon as possible, but it may not be immediately
     */
    @Override
    public void cancel() {
        running.set(false);
    }

    @Override
    public boolean isFinished() {
        return finished.get();
    }

    /**
     * Perform the work the task is intended for.
     * <p>
     * The running status must continuously be checked and the processing must stop when the task was canceled.
     */
    protected abstract void execute();
}
