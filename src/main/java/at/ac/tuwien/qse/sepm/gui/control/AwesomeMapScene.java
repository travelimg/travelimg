package at.ac.tuwien.qse.sepm.gui.control;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class AwesomeMapScene extends VBox {

    private static final Logger LOGGER = LogManager.getLogger();

    private final WebEngine webEngine;
    private final WebView webView;

    private Consumer<LatLong> clickCallback = null;
    private Consumer<LatLong> doubleClickCallback = null;

    private BooleanProperty loaded = new SimpleBooleanProperty(false);

    public AwesomeMapScene() {
        webView = new WebView();
        webEngine = webView.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((observable -> {
            if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                loaded.set(true);
            }
        }));

        webEngine.setOnError(new EventHandler<WebErrorEvent>() {
            @Override
            public void handle(WebErrorEvent event) {
                LOGGER.error("Error loading: {}", event, event.getException());

            }
        });

        webEngine.load(getClass().getClassLoader().getResource("html/map.html").toString());

        getChildren().add(webView);
    }

    public BooleanProperty getLoaded() {
        return loaded;
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
        callJS(String.format("document.addMarker(%f, %f);", latitude, longitude));
    }

    public void fitToMarkers() {
        callJS("document.fitToMarkers();");
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


}
