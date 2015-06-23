package at.ac.tuwien.qse.sepm.gui.control;

import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public class AwesomeMapScene extends VBox {

    private static final Logger LOGGER = LogManager.getLogger();

    private final WebEngine webEngine;
    private final WebView webView;

    private Consumer<LatLong> clickCallback = null;
    private Consumer<LatLong> doubleClickCallback = null;

    private Runnable onLoadedCallback = null;

    public AwesomeMapScene() {
        webView = new WebView();
        webEngine = webView.getEngine();

        webEngine.setOnAlert(this::handleAlert);
        webEngine.load(getClass().getClassLoader().getResource("html/map.html").toString());

        getChildren().add(webView);
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

    public void center(double latitude, double longitude) {
        LOGGER.debug("Centering map at ({}, {})", latitude, longitude);
        callJS(String.format("document.center(%f, %f);", latitude, longitude));
    }

    public void clear() {
        LOGGER.debug("Clearing map");
        callJS("document.clear();");
    }

    public void setZoom(int level) {
        LOGGER.debug("Set zoom level to {}", level);
        callJS(String.format("document.setZoom(%d);", level));
    }

    public void addMarker(double latitude, double longitude) {
        LOGGER.debug("Adding marker at ({}, {})", latitude, longitude);
        callJS(String.format(Locale.US, "document.addMarker(%f, %f);", latitude, longitude));
    }

    public void fitToMarkers() {
        callJS("document.fitToMarkers();");
    }

    /**
     * Draw a polyline path using a list of double pairs representing the vertices of the path.
     * @param vertices A ordered path of vertices which to draw.
     */
    public void drawPolyline(List<Pair<Double, Double>> vertices) {

        Optional<String> jsVertices = vertices.stream()
                .map(v -> String.format("[%f, %f]", v.getKey(), v.getValue()))
                .reduce((v1, v2) -> v1 + ", " + v2);

        if (jsVertices.isPresent()) {
            LOGGER.debug("Drawing polyline: {}", jsVertices.get());
            callJS("document.drawPolyline([" + jsVertices.get() + "]);");
        } else {
            LOGGER.debug("Failed to draw polyline: {}", vertices);
        }
    }

    private void handleDoubleClick(JSObject obj) {
        LOGGER.debug("Registered double click");

        if (doubleClickCallback != null) {
            doubleClickCallback.accept(null);
        }

    }

    private void handleClick(JSObject obj) {
        LOGGER.debug("Registered click");

        if (clickCallback != null) {
            clickCallback.accept(null);
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
                }
            } catch (NumberFormatException ex) {
                LOGGER.debug("Failed to parse click event from map");
            }
        }
    }

    public static class LatLong {
        private double latitude;
        private double longitude;

        public LatLong(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

}
