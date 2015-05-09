package at.ac.tuwien.qse.sepm.util;


import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CancelableTask implements Runnable, Cancelable {

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * Return the current execution status of the task.
     *
     * @return true if the task is currently executing else false.
     * */
    protected boolean getIsRunning() {
        return isRunning.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        isRunning.set(true);
        execute();
        isRunning.set(false);
    }

    /**
     * Cancel a running task.
     *
     * The processing will stop as soon as possible, but it may not be immediately
     */
    @Override
    public void cancel() {
        isRunning.set(false);
    }


    /**
     * Perform the work the task is intended for.
     *
     * The running status must continuously be checked and the processing must stop when the task was canceled.
     */
    protected abstract void execute();
}
