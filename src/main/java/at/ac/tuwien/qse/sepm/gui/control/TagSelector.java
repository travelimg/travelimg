package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.TagService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagSelector extends VBox {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private CheckListView<Tag> tagList;
    @FXML
    private TextField newCatName;
    @FXML
    private ToggleButton addCategoryBtn;
    @FXML
    private Button deleteTagBtn;
    private ListChangeListener<Tag> tagListChangeListener;

    private PhotoService photoservice;
    private TagService tagService;
    private Node root;

    /**
     * create Instance and initialize tagList.
     *
     * @param listener defines how to handle the change; must not be null;
     */
    public TagSelector(ListChangeListener<Tag> listener, PhotoService ps, TagService ts,
                       Node root) {
        LOGGER.debug("Instantiate TagSelector");
        this.tagListChangeListener = listener;
        this.photoservice = ps;
        this.tagService = ts;
        this.root = root;
        FXMLLoadHelper.load(this, this, TagSelector.class, "view/TagSelector.fxml");
        initializeTagList();
        addCategoryBtn.setOnAction(this::addCategory);
        deleteTagBtn.setOnAction(this::deleteSelectedTag);
        newCatName.setOnKeyReleased(this::highlightAddCategoryBtn);
    }

    public void initializeTagList() {
        ObservableList<Tag> tagNames = FXCollections.observableArrayList();
        try {
            tagNames.addAll(tagService.getAllTags());
        } catch (ServiceException ex) {
            ErrorDialog.show(getParent(), "Fehler beim Laden der Tags", "Fehlermeldung: " + ex.getMessage());
        }
        tagList.setItems(tagNames);

        tagList.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {
            @Override
            public ListCell<Tag> call(ListView<Tag> p) {
                return new CheckBoxListCell<Tag>(item -> tagList.getItemBooleanProperty(item),
                        new StringConverter<Tag>() {

                            @Override
                            public Tag fromString(String string) {
                                return null;
                            }

                            @Override
                            public String toString(Tag tag) {
                                return tag.getName();
                            }
                        });
            }
        });
        setPrefTagListHeight();
    }

    /**
     * calculate and set a good height according to nr of elements.
     */
    private void setPrefTagListHeight() {
        double height = 60.0;
        int nrOfElements = tagList.getItems().size();
        height += 38.0 * nrOfElements;

        if (height >= 300) {
            height = 300;
        }

        tagList.setPrefHeight(height);
    }

    /**
     * Load and show the tags which are currently set for <tt>photo</tt>.
     *
     * @param photo selected Photo
     */
    public void showCurrentlySetTags(Photo photo) {
        tagList.getCheckModel().getCheckedItems().removeListener(tagListChangeListener);
        tagList.getCheckModel().clearChecks();
        try {
            List<Tag> currentTags = tagService.getTagsForPhoto(photo);
            for (Tag tag : currentTags) {
                tagList.getCheckModel().check(tag);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(getParent(), "Fehler beim Laden von Tags", "Fehlermeldung: " + ex.getMessage());
        }
        tagList.getCheckModel().getCheckedItems().addListener(tagListChangeListener);
    }

    @FXML
    private void addCategory(ActionEvent event) {
        String newCategoryName = newCatName.getText();
        highlightAddCategoryBtn(null);

        if (isValidInput(newCategoryName)) {
            try {
                Tag newTag = tagService.create(new Tag(null, newCategoryName));
                LOGGER.info("Successfully added new category: \"{}\"", newCategoryName);
                addTagToList(newTag);
                newCatName.clear();
            } catch (ServiceException ex) {
                LOGGER.error("Failed to add new category: \"{}\"", newCategoryName);
                InfoDialog dialog = new InfoDialog(root, "Tag Fehler");
                dialog.setError(true);
                dialog.setHeaderText("Tag anlegen fehlgeschlagen");
                dialog.setContentText("Fehlermeldung: " + ex.getMessage());
                dialog.showAndWait();

            }

        } else {
            InfoDialog dialog = new InfoDialog(root, "Tag Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Tag anlegen fehlgeschlagen");
            dialog.setContentText(
                    "Fehlermeldung: Folgende Zeichen sind nicht erlaubt: ., /, travelimg");
            dialog.showAndWait();
            newCatName.requestFocus();
            newCatName.selectAll();
        }
        highlightAddCategoryBtn(null);
    }

    /**
     * Only concerns the GUI-list, not the persistence layer.
     *
     * @param newTag the Tag to add; must not be null;
     */
    private void addTagToList(Tag newTag) {
        tagList.getCheckModel().getCheckedItems().removeListener(tagListChangeListener);

        List<Tag> checkedTags = new ArrayList<>();
        for (Tag tag : tagList.getCheckModel().getCheckedItems()) {
            checkedTags.add(tag);
        }

        tagList.getCheckModel().clearChecks();
        tagList.getItems().add(newTag);

        for (Tag tag : checkedTags) {
            tagList.getCheckModel().check(tag);
        }
        LOGGER.info("Successfully added new Tag to shown list");

        setPrefTagListHeight();
        tagList.getCheckModel().getCheckedItems().addListener(tagListChangeListener);
    }

    @FXML
    private void highlightAddCategoryBtn(KeyEvent event) {
        if (isValidInput(newCatName.getText())) {
            addCategoryBtn.setSelected(true);
        } else {
            addCategoryBtn.setSelected(false);
        }
    }

    @FXML
    private void deleteSelectedTag(ActionEvent event) {
        Tag oldTag = tagList.getSelectionModel().getSelectedItem();

        if (oldTag != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Löschen bestätigen");
            alert.setHeaderText(
                    "Wollen Sie die Kategorie \"" + oldTag.getName() + "\" wirklich" + " löschen?");
            alert.setContentText("Alle damit verbundenen Daten gehen unwiderruflich verloren.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                try {
                    tagService.delete(oldTag);
                    LOGGER.info("Successfully deleted category: \"{}\"", oldTag);
                    removeFromList(oldTag);
                } catch (ServiceException ex) {
                    LOGGER.error("Failed to delete category: \"{}\"", oldTag);
                }
            } else {
                LOGGER.debug("User did not confirm deletion of category: \"{}\"", oldTag);
            }
        }
    }

    private void removeFromList(Tag oldTag) {
        tagList.getCheckModel().getCheckedItems().removeListener(tagListChangeListener);

        List<Tag> checkedTags = new ArrayList<>();
        for (Tag tag : tagList.getCheckModel().getCheckedItems()) {
            checkedTags.add(tag);
        }

        tagList.getCheckModel().clearChecks();
        tagList.getItems().remove(oldTag);
        checkedTags.remove(oldTag);

        for (Tag tag : checkedTags) {
            tagList.getCheckModel().check(tag);
        }
        LOGGER.info("Successfully deleted tag from shown list");
        setPrefTagListHeight();

        tagList.getCheckModel().getCheckedItems().addListener(tagListChangeListener);
    }

    // Validates user-tags for not allowed characters
    private boolean isValidInput(String string) {
        if (string.contains("/")) {
            return false;
        }
        if (string.contains(".")) {
            return false;
        }
        if (string.contains("travelimg")) {
            return false;
        }
        return string != null && !string.isEmpty() && string.trim().length() > 0;
    }
}