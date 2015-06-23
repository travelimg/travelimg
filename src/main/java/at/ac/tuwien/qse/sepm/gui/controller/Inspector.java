package at.ac.tuwien.qse.sepm.gui.controller;

import java.util.Collection;

/**
 * GUI component for viewing and editing the details of one or more entities.
 *
 * @param <E> type of entities that can be inspected
 */
public interface Inspector<E> {

    /**
     * Get the entities the inspector currently operates on.
     *
     * @return collection of entities
     */
    Collection<E> getEntities();

    /**
     * Set the entities the inspector should operate on.
     *
     * @param entities entities that should be operated on
     */
    void setEntities(Collection<E> entities);

    /**
     * Set a function that is invoked when the entities are modified.
     *
     * @param updateHandler function when the entities are modified
     */
    void setUpdateHandler(Runnable updateHandler);

    /**
     * Synchronizes the view to match the data available to the inspector.
     */
    void refresh();
}
