package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class PlaceFilter implements Predicate<Photo> {

    private Set<Place> includedPlaces = new HashSet<>();

    public Set<Place> getIncludedPlaces() {
        return includedPlaces;
    }

    @Override
    public boolean test(Photo photo) {
        return includedPlaces.contains(photo.getData().getPlace());
    }
}
