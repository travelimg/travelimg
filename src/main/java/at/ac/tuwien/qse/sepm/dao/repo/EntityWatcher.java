package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.function.Consumer;

/**
 * Watches the repository for changes to the stored entities and notifies listeners about changes
 */
public interface EntityWatcher {

    void subscribeTagAdded(Consumer<Tag> callback);
    void subscribePhotographerAdded(Consumer<Photographer> callback);
    void subscribeJourneyAdded(Consumer<Journey> callback);
    void subscribePlaceAdded(Consumer<Place> callback);
}
