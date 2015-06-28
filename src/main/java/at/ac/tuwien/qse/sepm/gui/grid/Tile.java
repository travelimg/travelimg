package at.ac.tuwien.qse.sepm.gui.grid;

import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tile extends StackPane {

    private static final Logger LOGGER = LogManager.getLogger();

    private boolean selected = false;

    /**
     * Marks this tile as selected. Has no effect if this tile is already selected.
     * Adds the 'selected' style class.
     */
    public void select() {
        if (selected) return;
        getStyleClass().add("selected");
        selected = true;
    }

    /**
     * Marks this tile as unselected. Has no effect if this tile is already unselected.
     * Removes the 'selected' style class.
     */
    public void deselect() {
        if (!selected) return;
        getStyleClass().removeAll("selected");
        selected = false;
    }

    /**
     * Get a value indicating that the tile is selected.
     *
     * @return true if it is selected, otherwise false
     */
    public boolean isSelected() {
        return selected;
    }
}
