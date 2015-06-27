package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.Filter;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

public class FilterSkin<T> extends SkinBase<Filter<T>> {

    private final FontAwesomeIconView icon = new FontAwesomeIconView();
    private final Label label = new Label();


    public FilterSkin(Filter control) {
        super(control);

        HBox container = new HBox();
        container.getChildren().addAll(icon, label);
        container.setOnMouseClicked((event) -> handleClick());
        getChildren().add(container);

        getSkinnable().valueProperty().addListener((observable) -> update());
        getSkinnable().includedProperty().addListener((observable) -> update());
        getSkinnable().countProperty().addListener((observable) -> update());
        update();
    }

    private void update() {

        // label with count
        String labelText = getSkinnable().getValue().toString();
        if (getSkinnable().isIncluded() && getSkinnable().isActive()) {
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
