package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MapSlideView extends SlideView {

    private final MapSlide slide;

    private GoogleMapScene mapScene = new GoogleMapScene();

    public MapSlideView(MapSlide slide, int height, int width) {
        this.slide = slide;

        Node overlay = createCaptionBox(slide.getCaption());
        StackPane.setAlignment(overlay, Pos.BOTTOM_CENTER);
        getChildren().addAll(mapScene, overlay);

        mapScene.setOnLoaded(this::addMarker);
    }

    private void addMarker() {
        LatLong position = new LatLong(slide.getLatitude(), slide.getLongitude());

        mapScene.addMarker(position);
        mapScene.center(position);
        mapScene.setZoom(slide.getZoomLevel());
    }
}
