package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.gui.control.AwesomeMapScene;

public class MapSlideView extends SlideView {

    private final MapSlide slide;

    private AwesomeMapScene mapScene = new AwesomeMapScene();

    public MapSlideView(MapSlide slide, int height, int width) {
        this.slide = slide;

        getChildren().add(mapScene);

        mapScene.getLoaded().addListener((observable) -> addMarker());
    }

    private void addMarker() {
        mapScene.addMarker(slide.getLatitude(), slide.getLongitude());
    }
}
