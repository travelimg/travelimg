package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.FilterControl;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

public class FilterSkin<T> extends SkinBase<FilterControl<T>> {

    private final FontAwesomeIconView icon = new FontAwesomeIconView();
    private final Label label = new Label();


    public FilterSkin(FilterControl control) {
        super(control);

        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        icon.getStyleClass().setAll("icon");
        container.getChildren().addAll(icon, label);
        getChildren().add(container);

        getSkinnable().setOnMouseClicked((event) -> handleClick());
        getSkinnable().valueProperty().addListener((observable) -> update());
        getSkinnable().includedProperty().addListener((observable) -> update());
        getSkinnable().countProperty().addListener((observable) -> update());
        getSkinnable().converterProperty().addListener((observable) -> update());
        update();
    }

    private void update() {

        String labelText = "Unbekannt";
        if (getSkinnable().getConverter() != null) {
            labelText = getSkinnable().getConverter().apply(getSkinnable().getValue());
        } else if (getSkinnable().getValue() != null) {
            labelText = getSkinnable().getValue().toString();
        }
        if (getSkinnable().isIncluded()) {
            labelText += String.format(" (%d)", getSkinnable().getCount());
        }
        label.setText(labelText);

        // icon
        if (getSkinnable().isIncluded()) {
            icon.setGlyphName("");
        }
    }

    private void handleClick() {
        getSkinnable().toggle();
    }
}
