package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.gui.control.AwesomeMapScene;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;

public class MapSlideView extends SlideView {

    private final MapSlide slide;

    private AwesomeMapScene mapScene = new AwesomeMapScene();

    public MapSlideView(MapSlide slide, int height, int width) {
        this.slide = slide;

        getChildren().add(mapScene);

        mapScene.setOnLoaded(this::addMarker);
    }

    private void addMarker() {
        mapScene.addMarker(new LatLong(slide.getLatitude(), slide.getLongitude()));
    }
}
