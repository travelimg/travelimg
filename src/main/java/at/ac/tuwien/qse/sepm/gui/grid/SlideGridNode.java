package at.ac.tuwien.qse.sepm.gui.grid;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SlideGridNode extends HBox {

    private final SlideTile tile;
    private final SlideDivider divider = new SlideDivider();

    public SlideGridNode(SlideTile tile) {
        if (tile == null) throw new IllegalArgumentException();
        this.tile = tile;

        getStyleClass().add("node");

        getChildren().addAll(divider, tile);
        HBox.setHgrow(tile, Priority.ALWAYS);
        HBox.setHgrow(divider, Priority.ALWAYS);
    }
}
