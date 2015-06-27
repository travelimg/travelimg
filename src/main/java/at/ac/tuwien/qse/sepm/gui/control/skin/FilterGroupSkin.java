package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.Filter;
import at.ac.tuwien.qse.sepm.gui.control.FilterGroup;
import at.ac.tuwien.qse.sepm.gui.control.OptionalContent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Set;

public class FilterGroupSkin<T> extends SkinBase<FilterGroup<T>> {

    private final VBox container = new VBox();

    private final HBox header = new HBox();
    private final FontAwesomeIconView icon = new FontAwesomeIconView();
    private final Label title = new Label();
    private final Button expand = new Button();

    private final OptionalContent body = new OptionalContent();
    private final Label placeholder = new Label();
    private final VBox list = new VBox();


    public FilterGroupSkin(FilterGroup<T> control) {
        super(control);

        icon.getStyleClass().setAll("icon");
        title.getStyleClass().setAll("title");
        expand.getStyleClass().setAll("expand-button");
        header.getStyleClass().setAll("header");
        header.getChildren().addAll(icon, title, expand);

        list.getStyleClass().setAll("list");
        body.getStyleClass().setAll("body");
        body.setContent(list);
        body.setPlaceholder(placeholder);

        container.getChildren().addAll(header, body);

        getSkinnable().titleProperty().addListener((observable) -> update());
        getSkinnable().expandedProperty().addListener((observable) -> update());
        getSkinnable().getItems().addListener(
                (ListChangeListener.Change<? extends Filter<T>> change) -> updateList());
    }

    private void update() {
        // expand
        body.setVisible(getSkinnable().isExpanded());
        body.setManaged(getSkinnable().isExpanded());

        // title
        title.setText(getSkinnable().getTitle());
    }

    private void updateList() {
        body.setAvailable(!getSkinnable().getItems().isEmpty());
        for (Filter<T> filter : getSkinnable().getItems()) {
            filter.includedProperty().addListener(observable -> {

            });
        }
    }
}
