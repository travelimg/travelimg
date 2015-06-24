package at.ac.tuwien.qse.sepm.gui.control.skin;

import at.ac.tuwien.qse.sepm.gui.control.TagControl;
import at.ac.tuwien.qse.sepm.gui.control.TagPicker;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class TagPickerSkin extends SkinBase<TagPicker> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final StackPane container;
    private final Label placeholder;
    private final VBox list;

    public TagPickerSkin(TagPicker control) {
        super(control);
        LOGGER.debug("creating instance");

        placeholder = new Label();
        placeholder.getStyleClass().setAll("placeholder");
        placeholder.setText("Keine Kategorien.");
        placeholder.setTextAlignment(TextAlignment.CENTER);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setMaxWidth(Double.MAX_VALUE);

        list = new VBox();

        container = new StackPane();
        container.getChildren().addAll(placeholder, list);

        getChildren().add(container);

        getSkinnable().getEntities().addListener(
                (ListChangeListener.Change<? extends Set<String>> change) -> updateList());
        updateList();
    }

    private void updateList() {
        LOGGER.debug("updating tag picker skin with tags {}", getSkinnable().getTags());
        boolean hasTags = getSkinnable().hasTags();
        placeholder.setVisible(!hasTags);
        placeholder.setManaged(!hasTags);
        list.setVisible(hasTags);
        list.setManaged(hasTags);

        list.getChildren().clear();
        for (String tag : getSkinnable().getTagsSorted()) {
            TagControl item = new TagControl();
            item.setName(tag);
            item.setCount(getSkinnable().count(tag));
            if (getSkinnable().isApplied(tag)) {
                item.setCount(-1);
            }
            item.setOnApply(() -> {
                getSkinnable().apply(tag);
                updateList();
            });
            item.setOnRemove(() -> {
                getSkinnable().remove(tag);
                updateList();
            });
            VBox.setVgrow(item, Priority.ALWAYS);
            list.getChildren().add(item);
        }
    }
}
