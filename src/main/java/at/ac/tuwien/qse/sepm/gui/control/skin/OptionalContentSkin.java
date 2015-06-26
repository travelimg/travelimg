package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.OptionalContent;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class OptionalContentSkin extends SkinBase<OptionalContent> {

    private final HBox placeholder = new HBox();
    private final HBox content = new HBox();

    public OptionalContentSkin(OptionalContent control) {
        super(control);

        content.getStyleClass().setAll("content");
        placeholder.getStyleClass().setAll("placeholder");

        StackPane container = new StackPane();
        container.getStyleClass().setAll("container");
        container.getChildren().addAll(placeholder, content);
        getChildren().addAll(container);

        getSkinnable().contentProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().placeholderProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().availableProperty().addListener((observable, oldValue, newValue) -> update());

        update();
    }

    private void update() {
        placeholder.getChildren().setAll(getSkinnable().getPlaceholder());
        content.getChildren().setAll(getSkinnable().getContent());
        placeholder.setVisible(!getSkinnable().getAvailable());
        content.setVisible(getSkinnable().getAvailable());
    }
}
