package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public class GoogleMapScene extends VBox {

    private static final Logger LOGGER = LogManager.getLogger();

    private final WebEngine webEngine;
    private final WebView webView;

    private Consumer<LatLong> clickCallback = null;
    private Consumer<LatLong> doubleClickCallback = null;
    private Consumer<LatLong> markerClickCallback = null;

    private Runnable onLoadedCallback = null;


    public GoogleMapScene() {
        webView = new WebView();
        EventDispatcher originalDispatcher = webView.getEventDispatcher();
        webView.setEventDispatcher(new CustomizedEventDispatcher(originalDispatcher));
        webEngine = webView.getEngine();

        webEngine.setOnAlert(this::handleAlert);
        webEngine.load(getClass().getClassLoader().getResource("html/map.html").toString());

        getChildren().add(webView);
        
        setVgrow(webView, Priority.ALWAYS);

    }

    public void setOnLoaded(Runnable callback) {
        this.onLoadedCallback = callback;
    }

    public void setClickCallback(Consumer<LatLong> clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void setDoubleClickCallback(Consumer<LatLong> doubleClickCallback) {
        this.doubleClickCallback = doubleClickCallback;
    }
    public void setMarkerClickCallback(Consumer<LatLong> markerClickCallback){
        this.markerClickCallback = markerClickCallback;
    }

    public void center(LatLong position) {
        LOGGER.debug("Centering map at ({}, {})", position.getLatitude(), position.getLongitude());
        callJS(String.format("document.center(%f, %f);", position.getLatitude(), position.getLongitude()));
    }

    public void clear() {
        LOGGER.debug("Clearing map");
        callJS("document.clear();");
    }

    public void setZoom(int level) {
        LOGGER.debug("Set zoom level to {}", level);
        callJS(String.format("document.setZoom(%d);", level));
    }

    public void addMarker(LatLong position) {
        LOGGER.debug("Adding marker at ({}, {})", position.getLatitude(), position.getLongitude());
        callJS(String.format(Locale.US, "document.addMarker(%f, %f, '');", position.getLatitude(), position.getLongitude()));
    }

    public void addMarker(LatLong position, String caption) {
        LOGGER.debug("Adding marker at ({}, {}) with caption {}", position.getLatitude(), position.getLongitude(), caption);
        callJS(String.format(Locale.US, "document.addMarker(%f, %f, '%s');", position.getLatitude(), position.getLongitude(), caption));
    }

    public void fitToMarkers() {
        callJS("document.fitToMarkers();");
    }

    public void fitToLastTwoMarkers() {
        callJS("document.fitToLastTwoMarkers();");
    }

    /**
     * Draw a polyline path using a list of vertices on the path.
     * @param vertices A ordered path of vertices which to draw.
     */
    public void drawPolyline(List<LatLong> vertices) {

        Optional<String> jsVertices = vertices.stream()
                .map(v -> String.format(Locale.US,"[%f, %f]", v.getLatitude(), v.getLongitude()))
                .reduce((v1, v2) -> v1 + ", " + v2);

        if (jsVertices.isPresent()) {
            LOGGER.debug("Drawing polyline: {}", jsVertices.get());
            callJS("document.drawPolyline([" + jsVertices.get() + "]);");
        } else {
            LOGGER.debug("Failed to draw polyline: {}", vertices);
        }
    }

    private void callJS(String script) {
        webEngine.executeScript(script);
    }

    private void handleAlert(WebEvent<String> event) {
        LOGGER.debug("Got alert {}", event);

        String data = event.getData();

        if (data.equals("map-loaded")) {
            if (onLoadedCallback != null) {
                onLoadedCallback.run();
            }
        } else if (data.contains("click")) {

            String type = data.substring(0, data.indexOf("("));
            String params = data.substring(data.indexOf("(") + 1, data.indexOf(")"));

            String latLng[] = params.split(",");

            if (latLng.length != 2) {
                return;
            }

            try {
                double latitude = Double.parseDouble(latLng[0].trim());
                double longitude = Double.parseDouble(latLng[1].trim());

                if (type.equals("double-click") && doubleClickCallback != null) {

                    doubleClickCallback.accept(new LatLong(latitude, longitude));


                } else if (type.equals("click") && clickCallback != null) {
                    clickCallback.accept(new LatLong(latitude, longitude));
                } else if (type.equals("marker-click") && markerClickCallback != null) {
                     markerClickCallback.accept(new LatLong(latitude,longitude));
                }
            } catch (NumberFormatException ex) {
                LOGGER.debug("Failed to parse click event from map");
            }
        }
    }

    private class CustomizedEventDispatcher implements EventDispatcher {

        private EventDispatcher originalDispatcher;

        public CustomizedEventDispatcher(EventDispatcher originalDispatcher) {
            this.originalDispatcher = originalDispatcher;
        }

        @Override
        public Event dispatchEvent(Event event, EventDispatchChain tail) {
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                //workaround to deny doubleclicks. this is
                // a jdk related bug: https://bugs.openjdk.java.net/browse/JDK-8092649
                if(mouseEvent.getClickCount()>=2){
                    mouseEvent.consume();
                }
            }
            return originalDispatcher.dispatchEvent(event, tail);
        }
    }
}
