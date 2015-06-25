package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;

public class MapSlideView extends SlideView {

    private final MapSlide slide;

    private GoogleMapScene mapScene = new GoogleMapScene();

    public MapSlideView(MapSlide slide, int height, int width) {
        this.slide = slide;

        getChildren().add(mapScene);

        mapScene.setOnLoaded(this::addMarker);
    }

    private void addMarker() {
        LatLong position = new LatLong(slide.getLatitude(), slide.getLongitude());

        mapScene.addMarker(position);
        mapScene.center(position);
    }
}
