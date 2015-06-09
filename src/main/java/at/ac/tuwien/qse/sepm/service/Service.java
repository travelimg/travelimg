package at.ac.tuwien.qse.sepm.service;

/**
 * Base interface for Services.
 */
public interface Service {

    /**
     * Closes associated resources.
     */
    default void close() {
    }
}