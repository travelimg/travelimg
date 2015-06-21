package at.ac.tuwien.qse.sepm.gui.control;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.DefaultProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

@DefaultProperty("children")
public class InspectorPane extends StackPane {

    private final Node placeholder;
    private final HBox selectionInfo = new HBox();
    private final Label selectionInfoLabel = new Label();
    private final VBox main = new VBox();
    private final VBox content = new VBox();

    public InspectorPane() {
        getStyleClass().add("inspector-pane");

        placeholder = createPlaceholder();
        super.getChildren().add(placeholder);
        selectionInfo.getStyleClass().add("selection-info");
        VBox.setVgrow(selectionInfoLabel, Priority.ALWAYS);
        selectionInfo.getChildren().add(selectionInfoLabel);
        selectionInfo.setAlignment(Pos.CENTER);

        main.getChildren().addAll(selectionInfo, content);
        super.getChildren().add(main);
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
        main.setVisible(hasActive);
        main.setManaged(hasActive);
        main.setMaxWidth(Double.MAX_VALUE);
        selectionInfo.setVisible(multiple);
        selectionInfo.setManaged(multiple);
        selectionInfoLabel.setText(Integer.toString(count) + " Elemente");
    }

    @Override public ObservableList<Node> getChildren() {
        return content.getChildren();
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
