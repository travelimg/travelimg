package at.ac.tuwien.qse.sepm.gui.control;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.DefaultProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class InspectorPane extends VBox {

    private final Node placeholder;
    private final HBox selectionInfo = new HBox();
    private final Label selectionInfoLabel = new Label();

    public InspectorPane() {
        getStyleClass().add("inspector-pane");

        placeholder = createPlaceholder();
        getChildren().add(placeholder);
        selectionInfo.getStyleClass().add("selection-info");
        VBox.setVgrow(selectionInfoLabel, Priority.ALWAYS);
        selectionInfo.getChildren().add(selectionInfoLabel);
        selectionInfo.setAlignment(Pos.CENTER);

        getChildren().add(selectionInfo);
        setCount(0);
    }

    private final IntegerProperty countProperty = new SimpleIntegerProperty();
    public IntegerProperty countProperty() {
        return countProperty;
    }
    public int getCount() {
        return countProperty().get();
    }
    public void setCount(int count) {
        countProperty().set(count);

        boolean hasNone = count == 0;
        boolean hasActive = !hasNone;
        boolean multiple = count > 1;
        placeholder.setVisible(hasNone);
        placeholder.setManaged(hasNone);
        selectionInfo.setVisible(multiple);
        selectionInfo.setManaged(multiple);
        selectionInfoLabel.setText(Integer.toString(count) + " Elemente");

        // en/disable content
        for (Node node : getChildren()) {
            if (node != placeholder && node != selectionInfo) {
                node.setVisible(hasActive);
                node.setManaged(hasActive);
            }
        }
    }

    private static Node createPlaceholder() {
        VBox placeholder = new VBox();
        placeholder.getStyleClass().add("placeholder");
        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName("CAMERA");
        Label label = new Label();
        label.setText("Keine Elemente ausgewählt.");
        placeholder.getChildren().addAll(icon, label);
        return placeholder;
    }
}
