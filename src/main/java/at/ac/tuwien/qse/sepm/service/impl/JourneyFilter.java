package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class JourneyFilter implements Predicate<Photo> {

    private Set<Journey> includedJourneys = new HashSet<>();

    public Set<Journey> getIncludedJourneys() {
        return includedJourneys;
    }

    @Override
    public boolean test(Photo photo) {
        return includedJourneys.contains(photo.getData().getJourney());
    }
}
