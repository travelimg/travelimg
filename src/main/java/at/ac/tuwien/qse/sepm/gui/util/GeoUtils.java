package at.ac.tuwien.qse.sepm.gui.util;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.GoogleMapsScene;
import com.lynden.gmapsfx.javascript.object.LatLong;

import java.util.List;

public class GeoUtils {

    /**
     * Fits markers to the google maps scene
     * @param path an array containing latlong objects (geo data of the markers)
     * @param from position on the path where to start
     * @param to position on the path where to end
     * @param mapHeight the height of the map container
     * @param mapWidth the width of the map container
     * @param mapsScene the google maps scene
     */
    public static void fitMarkersToScreen(LatLong[] path, int from, int to, double mapHeight, double mapWidth, GoogleMapsScene mapsScene) {
        Double ne_lat = null;
        Double ne_long = null;
        Double sw_lat = null;
        Double sw_long = null;
        for(int i = from; i<=to && i<path.length;i++) {
            if (ne_lat == null) {
                ne_lat = path[i].getLatitude();
            }
            if (ne_long == null) {
                ne_long = path[i].getLongitude();
            }
            if (sw_lat == null) {
                sw_lat = path[i].getLatitude();
            }
            if (sw_long == null) {
                sw_long = path[i].getLongitude();
            }
            if (path[i].getLatitude() > ne_lat) {
                ne_lat = path[i].getLatitude();
            }
            if (path[i].getLongitude() > ne_long) {
                ne_long = path[i].getLongitude();
            }
            if (path[i].getLatitude() < sw_lat) {
                sw_lat = path[i].getLatitude();
            }
            if (path[i].getLongitude() < sw_long) {
                sw_long = path[i].getLongitude();
            }
        }
        LatLong ne = new LatLong(ne_lat,ne_long);
        LatLong sw = new LatLong(sw_lat,sw_long);
        double latFraction = ((ne.latToRadians()) - sw.latToRadians()) / Math.PI;
        double lngDiff = ne.getLongitude() - sw.getLongitude();
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = Math.floor(Math.log(mapHeight / 256 / latFraction) / 0.6931472);
        double lngZoom = Math.floor(Math.log((mapWidth) / 256 / lngFraction) / 0.6931472);
        double min = Math.min(latZoom, lngZoom);
        min = Math.min(min,21);
        mapsScene.setZoom((int) min);
        mapsScene.setCenter((ne.getLatitude() + sw.getLatitude()) / 2,
                (ne.getLongitude() + sw.getLongitude()) / 2);
    }

    /**
     * Converts a list of places to a latlong array
     * @param places the list of places
     * @return the converted array of latlong objects
     */
    public static LatLong[] toLatLong(List<Place> places){
        LatLong[] path = new LatLong[places.size()];
        for(int i = 0; i<places.size(); i++){
            path[i] = new LatLong(places.get(i).getLatitude(),places.get(i).getLongitude());
        }
        return path;
    }

}
