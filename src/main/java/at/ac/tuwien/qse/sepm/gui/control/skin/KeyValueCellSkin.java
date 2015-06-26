package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.KeyValueCell;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;

public class KeyValueCellSkin extends SkinBase<KeyValueCell> {

    private final Label keyLabel = new Label();
    private final Label valueLabel = new Label();

    public KeyValueCellSkin(KeyValueCell control) {
        super(control);

        keyLabel.getStyleClass().setAll("key");
        valueLabel.getStyleClass().setAll("value");

        BorderPane container = new BorderPane();
        container.setLeft(keyLabel);
        container.setRight(valueLabel);

        getChildren().add(container);

        getSkinnable().keyProperty().addListener((obs, v1, v2) -> updateKey());
        getSkinnable().valueProperty().addListener((obs, v1, v2) -> updateValue());
        getSkinnable().indeterminedProperty().addListener((obs, v1, v2) -> updateIndetermined());

        update();
    }

    private void update() {
        updateKey();
        updateValue();
        updateIndetermined();
    }

    private void updateKey() {
        keyLabel.setText(getSkinnable().getKey());
    }

    private void updateValue() {
        String value = getSkinnable().getValue();
        if (value == null || value.isEmpty()) {
            value = "kein Wert";
        }
        valueLabel.setText(value);
    }

    private void updateIndetermined() {
        if (getSkinnable().isIndetermined()) {
            valueLabel.setText("mehrdeutig");
        }
    }
}
