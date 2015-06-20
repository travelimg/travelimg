package at.ac.tuwien.qse.sepm.gui.grid;

import javafx.scene.layout.HBox;

public class SlideGridNode extends HBox {

    private final SlideTile tile;
    private final SlideDivider divider = new SlideDivider();

    public SlideGridNode(SlideTile tile) {
        if (tile == null) throw new IllegalArgumentException();
        this.tile = tile;

        getChildren().addAll(divider, tile);
    }
}
