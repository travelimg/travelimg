package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.Filter;
import at.ac.tuwien.qse.sepm.gui.control.FilterGroup;
import at.ac.tuwien.qse.sepm.gui.control.OptionalContent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.*;

import java.util.Collection;
import java.util.Set;

public class FilterGroupSkin<T> extends SkinBase<FilterGroup<T>> {

    private final VBox container = new VBox();

    private final BorderPane header = new BorderPane();
    private final HBox headerLeft = new HBox();
    private final FontAwesomeIconView icon = new FontAwesomeIconView();
    private final Label title = new Label();
    private final Button expand = new Button();
    private final FontAwesomeIconView expandIcon = new FontAwesomeIconView();

    private final OptionalContent body = new OptionalContent();
    private final Label placeholder = new Label();
    private final VBox list = new VBox();

    public FilterGroupSkin(FilterGroup<T> control) {
        super(control);

        icon.getStyleClass().setAll("check-icon");
        title.getStyleClass().setAll("title");
        expand.getStyleClass().setAll("expand-button");
        expandIcon.getStyleClass().setAll("icon");
        expand.setGraphic(expandIcon);
        headerLeft.getStyleClass().setAll("left");
        headerLeft.setAlignment(Pos.CENTER_LEFT);
        headerLeft.getChildren().addAll(icon, title);
        header.getStyleClass().setAll("header");
        header.setLeft(headerLeft);
        header.setRight(expand);

        placeholder.setText("Keine Optionen verfügbar.");

        list.getStyleClass().setAll("list");
        body.getStyleClass().setAll("body");
        body.setContent(list);
        body.setPlaceholder(placeholder);

        container.getChildren().addAll(header, body);
        getChildren().add(container);

        getSkinnable().titleProperty().addListener((observable) -> update());
        getSkinnable().expandedProperty().addListener((observable) -> update());
        getSkinnable().getItems().addListener(
                (ListChangeListener.Change<? extends Filter<T>> change) -> updateList());

        header.setOnMouseClicked(event -> getSkinnable().toggleExpansion());
        icon.setOnMouseClicked(event -> {
            getSkinnable().toggleAll();
            event.consume();
        });

        update();
        updateList();
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
        list.getChildren().clear();
        for (Filter<T> filter : getSkinnable().getItems()) {
            list.getChildren().add(filter);
        }
    }
}
