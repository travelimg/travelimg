package at.ac.tuwien.qse.sepm.util;


public interface Cancelable {

    /**
     * Cancel a running operation.
     */
    void cancel();

    /**
     * Poll if the cancelable is finished.
     *
     * @return the completion status of this cancelable.
     */
    boolean isFinished();
}
