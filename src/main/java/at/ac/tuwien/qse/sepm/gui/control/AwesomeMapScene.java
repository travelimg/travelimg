package at.ac.tuwien.qse.sepm.gui.control;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import javafx.scene.layout.VBox;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class AwesomeMapScene extends VBox implements MapComponentInitializedListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private GoogleMapView view;
    private Collection<Marker> markers = new ArrayList<>();

    private Consumer<LatLong> clickCallback = null;
    private Consumer<LatLong> doubleClickCallback = null;

    public AwesomeMapScene() {
        this.view = new GoogleMapView();
        this.view.addMapInializedListener(this);
    }

    @Override
    public void mapInitialized() {
        if (isInitialized()) {
            return;
        }

        LOGGER.debug("Map initialized");

        try {
            view.createMap(createMapOptions());
            getChildren().add(view);

            getMap().addUIEventHandler(UIEventType.click, this::handleClick);
            getMap().addUIEventHandler(UIEventType.dblclick, this::handleDoubleClick);
        } catch (JSException ex) {
            // ignore
        }
    }

    public void setClickCallback(Consumer<LatLong> clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void setDoubleClickCallback(Consumer<LatLong> doubleClickCallback) {
        this.doubleClickCallback = doubleClickCallback;
    }

    public void center(double latitude, double longitude) {
        view.setCenter(latitude, longitude);
    }

    public void clear() {
        if (!isInitialized()) {
            mapInitialized();
            return;
        }

        try {
            markers.forEach((marker) -> getMap().removeMarker(marker));
            markers.clear();
        } catch (JSException ex) {
            // ignore
        }
    }

    public void addMarker(double latitude, double longitude) {
        LOGGER.debug("Adding marker at ({}, {})", latitude, longitude);

        if (!isInitialized()) {
            mapInitialized();
            return;
        }

        try {
            Marker marker = createMarkerAt(latitude, longitude);
            markers.add(marker);

             getMap().addMarker(marker);
        } catch (JSException ex) {
            // ignore
        }
    }

    private void handleDoubleClick(JSObject obj) {
        LOGGER.debug("Registered double click");

        LatLong position = new LatLong((JSObject)obj.getMember("latLng"));
        doubleClickCallback.accept(position);
    }

    private void handleClick(JSObject obj) {
        LOGGER.debug("Registered click");

        LatLong position = new LatLong((JSObject)obj.getMember("latLng"));
        clickCallback.accept(position);
    }

    private GoogleMap getMap() {
        return view.getMap();
    }

    private boolean isInitialized() {
        return getMap() != null;
    }

    private MapOptions createMapOptions() {
        MapOptions options = new MapOptions();
        options.center(new LatLong(39.7385, -104.9871))
                .overviewMapControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .mapTypeControl(false)
                .zoom(2)
                .mapMarker(true);

        return options;
    }

    private Marker createMarkerAt(double latitude, double longitude) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLong(latitude, longitude))
                .visible(true);

        return new Marker(options);
    }
}
