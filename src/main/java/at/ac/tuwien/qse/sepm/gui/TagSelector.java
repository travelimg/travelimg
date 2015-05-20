package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;

import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class TagSelector extends VBox {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML private CheckListView<Tag> tagList;
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
}
