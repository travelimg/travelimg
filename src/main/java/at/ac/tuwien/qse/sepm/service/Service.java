package at.ac.tuwien.qse.sepm.service;

public interface Service {

    /**
     * Closes associated resources.
     */
    default void close() {
    }
}