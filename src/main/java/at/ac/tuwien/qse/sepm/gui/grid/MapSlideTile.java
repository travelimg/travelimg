package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.image.ImageView;

public class MapSlideTile extends SlideTileBase<MapSlide> {

    private final FontAwesomeIconView icon = new FontAwesomeIconView();
    private final ImageView image = new ImageView();

    public MapSlideTile(MapSlide slide) {
        super(slide);
        getStyleClass().add("map");

        getChildren().add(0, icon);
        getChildren().add(0, image);

        image.setPreserveRatio(false);
        image.setFitWidth(ImageSize.MEDIUM.pixels());
        image.setFitHeight(ImageSize.MEDIUM.pixels());
    }
}
