package at.ac.tuwien.qse.sepm.gui.control;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckItem extends HBox {

    private static final Logger LOGGER = LogManager.getLogger();

    private final FontAwesomeIconView icon = new FontAwesomeIconView();
    private final Label label = new Label();
    private final ObjectProperty<CheckState> stateProperty =
            new SimpleObjectProperty<>(this, "state", CheckState.UNCHECKED);

    public CheckItem() {
        getStyleClass().add("check-item");
        getChildren().addAll(icon, label);

        setAlignment(Pos.CENTER_LEFT);

        setOnMouseClicked(this::handleClick);
        update();
    }

    public ObjectProperty<CheckState> stateProperty() {
        return stateProperty;
    }

    public CheckState getState() {
        return stateProperty().get();
    }

    public void setState(CheckState state) {
        stateProperty().set(state);
        update();
    }

    public void setText(String text) {
        label.setText(text);
    }

    private void update() {
        getStyleClass().removeAll("indetermined", "checked", "unchecked");
        switch (getState()) {
            case UNCHECKED:
                icon.setGlyphName("SQUARE");
                getStyleClass().add("unchecked");
                break;
            case CHECKED:
                icon.setGlyphName("CHECK_SQUARE");
                getStyleClass().add("checked");
                break;
            case INDETERMINED:
                icon.setGlyphName("MINUS_SQUARE");
                getStyleClass().add("indetermined");
                break;
        }
    }

    private void handleClick(Event event) {
        LOGGER.debug("handle click, current state is {}", getState());
        switch (getState()) {
            case INDETERMINED:
            case UNCHECKED:
                setState(CheckState.CHECKED);
                break;
            case CHECKED:
                setState(CheckState.UNCHECKED);
                break;
        }
    }
}