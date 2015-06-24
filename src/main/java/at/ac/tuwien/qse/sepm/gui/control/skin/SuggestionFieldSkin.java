package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.SuggestionField;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuggestionFieldSkin extends SkinBase<SuggestionField> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final TextField field;
    private final Label label;
    private final Button suggestionText;

    public SuggestionFieldSkin(SuggestionField control) {
        super(control);

        field = new TextField();

        label = new Label();
        label.getStyleClass().setAll("label");
        suggestionText = new Button();
        suggestionText.getStyleClass().setAll("suggestion");
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(label, suggestionText);

        VBox container = new VBox();
        VBox.setVgrow(box, Priority.ALWAYS);
        VBox.setVgrow(field, Priority.ALWAYS);
        container.getChildren().addAll(box, field);

        getChildren().add(container);

        suggestionText.setOnAction(event -> {
            getSkinnable().setText(suggestionText.getText());
            getSkinnable().confirm();
        });
        field.setOnAction(event -> getSkinnable().confirm());
        getSkinnable().textProperty().bindBidirectional(field.textProperty());
        getSkinnable().textProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().labelProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().getSuggestions().addListener(
                (ListChangeListener.Change<? extends String> change) -> update());

        update();
    }

    private void update() {
        LOGGER.debug("updating with text {} and suggestion {}",
                getSkinnable().getText(),
                getSkinnable().getSuggestion());

        String suggestionValue = getSkinnable().getSuggestion();
        boolean hasSuggestion = suggestionValue != null && !suggestionValue.isEmpty();
        suggestionText.setVisible(hasSuggestion);
        if (hasSuggestion) {
            label.setText("Vorschlag: ");
        } else {
            label.setText(getSkinnable().getLabel());
        }
        suggestionText.setText(suggestionValue);

        field.setText(getSkinnable().getText());
    }
}
