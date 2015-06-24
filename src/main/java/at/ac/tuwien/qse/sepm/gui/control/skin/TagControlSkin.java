package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.TagControl;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class TagControlSkin extends SkinBase<TagControl> {

    private static final String REMOVE_BUTTON_GLYPH_NAME = "TIMES";
    private static final String APPLIED_GLYPH_NAME = "CHECK_SQUARE";
    private static final String PARTIAL_GLYPH_NAME = "MINUS_SQUARE";
    private static final String REMOVED_GLYPH_NAME = "SQUARE";

    private final Label label;
    private final Button applyButton;
    private final FontAwesomeIconView icon;
    private final Button removeButton;

    public TagControlSkin(TagControl control) {
        super(control);

        icon = new FontAwesomeIconView();
        icon.getStyleClass().setAll("check-icon");
        icon.setGlyphSize(20);
        icon.setTextAlignment(TextAlignment.CENTER);

        applyButton = new Button();
        applyButton.getStyleClass().setAll("apply-button");
        applyButton.setAlignment(Pos.CENTER_LEFT);
        applyButton.setTextAlignment(TextAlignment.CENTER);
        applyButton.setGraphic(icon);

        label = new Label();
        label.getStyleClass().setAll("label");
        label.setAlignment(Pos.CENTER_LEFT);
        label.setTextAlignment(TextAlignment.LEFT);

        FontAwesomeIconView removeIcon = new FontAwesomeIconView();
        removeIcon.setGlyphName(REMOVE_BUTTON_GLYPH_NAME);
        removeIcon.setGlyphSize(20);
        removeIcon.setFill(Color.GRAY);
        removeButton = new Button();
        removeButton.getStyleClass().setAll("remove");
        removeButton.setAlignment(Pos.CENTER_RIGHT);
        removeButton.setGraphic(removeIcon);
        getChildren().add(removeButton);

        HBox container = new HBox();
        container.getChildren().addAll(applyButton, label, removeButton);
        HBox.setHgrow(applyButton, Priority.ALWAYS);
        HBox.setHgrow(label, Priority.ALWAYS);
        HBox.setHgrow(removeButton, Priority.ALWAYS);
        getChildren().add(container);

        // Register listeners.
        control.nameProperty().addListener((observable, oldValue, newValue) -> update());
        control.countProperty().addListener((observable, oldValue, newValue) -> update());
        applyButton.setOnAction(this::handleApply);
        removeButton.setOnAction(this::handleRemove);

        update();
    }

    private void update() {
        String text = getSkinnable().getName();
        if (text == null) {
            text = "";
        }
        if (getSkinnable().isPartial()) {
            text += " (" + getSkinnable().getCount() + ")";
        }
        label.setText(text);

        if (getSkinnable().isPartial()) {
            icon.setGlyphName(PARTIAL_GLYPH_NAME);
        } else if (getSkinnable().isApplied()) {
            icon.setGlyphName(APPLIED_GLYPH_NAME);
        } else {
            icon.setGlyphName(REMOVED_GLYPH_NAME);
        }
    }

    private void handleApply(Event event) {
        event.consume();
        getSkinnable().apply();
    }

    private void handleRemove(Event event) {
        event.consume();
        getSkinnable().remove();
    }
}
