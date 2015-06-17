package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.Slideshow;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;
public class SlideGridTile extends ImageGridTile {

    private static final Logger LOGGER = LogManager.getLogger();

    private final BorderPane overlay = new BorderPane();
    private final Label left = new Label();
    private final Label right = new Label();

    private Consumer<SlideGridTile> leftClickedCallback = null;
    private Consumer<SlideGridTile> rightClickedCallback = null;

    public SlideGridTile() {
        getStyleClass().add("slide-tile");
        overlay.getStyleClass().add("overlay");

        left.getStyleClass().add("left");
        right.getStyleClass().add("right");

        FontAwesomeIconView leftIconView = new FontAwesomeIconView();
        leftIconView.setGlyphName("ARROW_LEFT");
        left.setGraphic(leftIconView);

        FontAwesomeIconView rightIconView = new FontAwesomeIconView();
        rightIconView.setGlyphName("ARROW_RIGHT");
        right.setGraphic(rightIconView);

        overlay.setLeft(left);
        overlay.setRight(right);

        setAlignment(left, Pos.CENTER_LEFT);
        setAlignment(right, Pos.CENTER_RIGHT);

        getChildren().add(overlay);

        left.setOnMouseClicked(event -> {
            if (leftClickedCallback != null)
                leftClickedCallback.accept(this);
        });

        right.setOnMouseClicked(event -> {
            if (rightClickedCallback != null)
                rightClickedCallback.accept(this);
        });
    }

    public void onLeftClicked(Consumer<SlideGridTile> callback) {
        this.leftClickedCallback = callback;
    }

    public void onRightClicked(Consumer<SlideGridTile> callback) {
        this.rightClickedCallback = callback;
    }
}
