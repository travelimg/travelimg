package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;

import java.util.List;
import java.util.Optional;

public class TagSelector extends VBox {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML private CheckListView<Tag> tagList;
    @FXML private TextField newCatName;
    @FXML private ToggleButton addCategoryBtn;
    private ListChangeListener<Tag> tagListChangeListener;

    private PhotoService photoservice;

    /**
     * create Instance and initialize tagList.
     *
     * @param listener defines how to handle the change; must not be null;
     */
    public TagSelector(ListChangeListener<Tag> listener, PhotoService ps) {
        LOGGER.debug("Instantiate TagSelector");
        this.tagListChangeListener = listener;
        this.photoservice = ps;
        FXMLLoadHelper.load(this, this, TagSelector.class, "view/TagSelector.fxml");
        initializeTagList();
        addCategoryBtn.setOnAction(this::addCategory);
        newCatName.setOnKeyReleased(this::highlightAddCategoryBtn);
    }

    private void initializeTagList() {
        ObservableList<Tag> tagNames = FXCollections.observableArrayList();
        try {
            for (Tag tag : photoservice.getAllTags()) {
                tagNames.add(tag);
            }
        } catch (ServiceException ex) {
            //TODO Dialog
        }
        tagList.setItems(tagNames);

        tagList.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {
            @Override public ListCell<Tag> call(ListView<Tag> p) {
                return new CheckBoxListCell<Tag>(item -> tagList.getItemBooleanProperty(item),
                        new StringConverter<Tag>() {

                            @Override public Tag fromString(String string) {
                                return null;
                            }

                            @Override public String toString(Tag tag) {
                                return tag.getName();
                            }
                        });
            }
        });
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
            List<Tag> currentTags = photoservice.getTagsForPhoto(photo);
            for (Tag tag : currentTags) {
                tagList.getCheckModel().check(tag);
            }
        } catch (ServiceException ex) {
            //TODO Dialog
        }
        tagList.getCheckModel().getCheckedItems().addListener(tagListChangeListener);
    }

    @FXML
    private void addCategory(ActionEvent event) {
        String newCategoryName = newCatName.getText();
        highlightAddCategoryBtn(null);

        if (isValidInput(newCategoryName)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Hinzufügen bestätigen");
            alert.setHeaderText("Wollen Sie die Kategorie \"" + newCategoryName + "\" wirklich"
                    + " hinzufügen?");
            alert.setContentText("Im Moment kann die Kategorie dann nicht mehr gelöscht werden");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK){
                LOGGER.info("Successfully added new category: \"{}\"", newCategoryName);
                newCatName.clear();
            } else {
                LOGGER.debug("User did not confirm addition of category: \"{}\"", newCategoryName);
                newCatName.requestFocus();
                newCatName.selectAll();
            }
        } else {
            /*
            alert.setTitle("Fehlende Eingabe");
            alert.setHeaderText("Es wurde noch kein gültiger Name für die Kategorie gewählt");
            alert.setContentText("Bitte geben Sie den gewünschten Namen in das Textfeld links "
                    + "des 'Plus'-Buttons ein und versuchen Sie es erneut.");

            alert.showAndWait();
            */
            newCatName.requestFocus();
            newCatName.selectAll();
        }
        highlightAddCategoryBtn(null);
    }

    @FXML
    private void highlightAddCategoryBtn(KeyEvent event) {
        if (isValidInput(newCatName.getText())) {
            addCategoryBtn.setSelected(true);
        } else {
            addCategoryBtn.setSelected(false);
        }
    }

    private boolean isValidInput(String string) {
        return string != null
                && !string.isEmpty()
                && string.trim().length() > 0;
    }
}
