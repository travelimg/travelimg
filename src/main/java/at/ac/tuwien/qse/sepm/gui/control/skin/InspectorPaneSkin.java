package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.control.OptionalContent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InspectorPaneSkin extends SkinBase<InspectorPane> {

    private final OptionalContent root = new OptionalContent();
    private final VBox header = new VBox();
    private final ScrollPane body = new ScrollPane();
    private final Label placeholderLabel = new Label();
    private final VBox selectionInfo = new VBox();
    private final Label selectionInfoLabel = new Label();

    public InspectorPaneSkin(InspectorPane control) {
        super(control);

        VBox placeholder = new VBox();
        placeholder.setAlignment(Pos.CENTER);
        FontAwesomeIconView placeholderIcon = new FontAwesomeIconView();
        placeholderIcon.setGlyphName("CAMERA");
        placeholder.getChildren().addAll(placeholderIcon, placeholderLabel);

        selectionInfo.getStyleClass().add("selection-info");
        selectionInfo.setAlignment(Pos.CENTER);
        VBox.setVgrow(selectionInfoLabel, Priority.ALWAYS);
        selectionInfo.getChildren().add(selectionInfoLabel);

        header.getStyleClass().setAll("header");

        body.getStyleClass().setAll("body");
        body.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        body.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setPlaceholder(placeholder);
        root.setContent(new VBox(header, body));
        getChildren().add(root);

        getSkinnable().headerProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().bodyProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().entityNameProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().countProperty().addListener((observable, oldValue, newValue) -> update());

        update();
    }

    private void update() {
        header.getChildren().setAll(selectionInfo);
        if (getSkinnable().getHeader() != null) {
            header.getChildren().add(getSkinnable().getHeader());
        }
        body.setContent(getSkinnable().getBody());

        String entityName = getSkinnable().getEntityName();
        if (entityName == null || entityName.isEmpty()) {
            entityName = "Elemente";
        }
        int count = getSkinnable().getCount();
        placeholderLabel.setText(String.format("Keine %s ausgewählt.", entityName));
        root.setAvailable(count != 0);
        selectionInfo.setVisible(count > 1);
        selectionInfo.setManaged(count > 1);
        selectionInfoLabel.setText(String.format("%d %s ausgewählt", count, entityName));
    }
}
