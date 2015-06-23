package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;

import java.util.List;
import java.util.stream.Collectors;

public class TravelRouteMap extends GoogleMapScene {

    public void drawJourney(List<Place> places) {
        clear();

        if (places.isEmpty()) {
            return;
        }

        if (places.size() == 1) {
            Place place = places.get(0);
            LatLong position = new LatLong(place.getLatitude(), place.getLongitude());
            addMarker(position);
        } else {
            List<LatLong> path = places.stream()
                    .map(p -> new LatLong(p.getLatitude(), p.getLongitude()))
                    .collect(Collectors.toList());

            drawPolyline(path);
        }
    }
}
