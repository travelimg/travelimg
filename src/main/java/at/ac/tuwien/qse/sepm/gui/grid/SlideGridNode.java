package at.ac.tuwien.qse.sepm.gui.grid;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

    public SlideTile getTile() {
        return tile;
    }

    public void setSlidePositionChangeCallback(BiConsumer<SlideTile, Integer> callback) {
        divider.setSlideDroppedCallback((sourceId) -> {
            if (sourceId.equals(tile.getSlide().getId())) {
                return; // ignore drop on itself
            }

            callback.accept(tile, sourceId);
        });
    }

    public void setSlideAddedCallback(Consumer<SlideGridNode> callback) {
        divider.setSlideAddedCallback(() -> callback.accept(this));
    }
}