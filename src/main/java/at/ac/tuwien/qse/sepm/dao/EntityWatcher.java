package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.function.Consumer;

/**
 * Watcher for changes to entities
 */
public interface EntityWatcher<E> {

    /**
     * Registers the callback which is invoked when ever an entity is added.
     * @param callback The callback to register.
     */
    void subscribeAdded(Consumer<E> callback);
}
