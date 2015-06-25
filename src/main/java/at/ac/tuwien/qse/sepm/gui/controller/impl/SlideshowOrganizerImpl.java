package at.ac.tuwien.qse.sepm.gui.controller.impl;


import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.controller.SlideshowOrganizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.SlideshowServiceImpl;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class SlideshowOrganizerImpl implements SlideshowOrganizer {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NEW_SLIDESHOW_DEFAULT_NAME = "Neue Präsentation";

    @FXML
    private ListView<Slideshow> slideshowList;
    @FXML
    private BorderPane root;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button presentButton;
    @FXML
    private VBox slideshowPropertiesBox;
    @FXML
    private TextField slideshowNameTextField;
    @FXML
    private RadioButton shortDurationButton;
    @FXML
    private RadioButton mediumDurationButton;
    @FXML
    private RadioButton longDurationButton;
    private ToggleGroup durationToggleGroup = new ToggleGroup();

    @Autowired
    private SlideshowServiceImpl slideshowService;

    private Consumer<Slideshow> slideshowAddedCallback = null;
    private Consumer<Slideshow> slideshowDeletedCallback = null;

    private ObjectProperty<Slideshow> selectedSlideshowProperty = new SimpleObjectProperty<>(null);


    public ObjectProperty<Slideshow> getSelectedSlideshowProperty() {
        return selectedSlideshowProperty;
    }

    public Slideshow getSelected() {
        return selectedSlideshowProperty.get();
    }

    public void setSlideshows(ObservableList<Slideshow> slideshows) {
        slideshowList.setItems(FXCollections.observableArrayList()); // clear list
        
        // only display real slideshows (no placeholder)
        FilteredList<Slideshow> filtered = slideshows.filtered(s -> s.getId() >= 0);
        slideshowList.setItems(filtered);
    }

    @Override
    public void setPresentAction(Runnable callback) {
        LOGGER.debug("setting present action");
        presentButton.setOnAction(event -> callback.run());
    }

    @Override
    public void setAddAction(Consumer<Slideshow> callback) {
        this.slideshowAddedCallback = callback;
    }

    @Override public void setDeleteAction(Consumer<Slideshow> callback) {
        this.slideshowDeletedCallback = callback;
    }

    @FXML
    private void initialize() {
        slideshowList.setCellFactory(new SlideshowCellFactory());

        // add Buttons to toggle Group
        shortDurationButton.setSelected(true);
        shortDurationButton.setToggleGroup(durationToggleGroup);
        mediumDurationButton.setToggleGroup(durationToggleGroup);
        longDurationButton.setToggleGroup(durationToggleGroup);

        addButton.setOnAction(this::handleAddSlideshow);
        deleteButton.setOnAction(this::handleDeleteSlideshow);
        slideshowNameTextField.textProperty().addListener(this::updateSelectedSlideshowName);
        durationToggleGroup.selectedToggleProperty().addListener(this::updateSlideshowDuration);
        slideshowList.getSelectionModel().selectedItemProperty().addListener(this::onActiveSlideshowChanged);

        slideshowPropertiesBox.visibleProperty().bind(selectedSlideshowProperty.isNotNull());
        slideshowPropertiesBox.managedProperty().bind(slideshowPropertiesBox.visibleProperty());
    }

    private void refreshSlideshowList() {
        int selectedIndex = slideshowList.getSelectionModel().getSelectedIndex();

        ObservableList<Slideshow> slideshows = slideshowList.getItems();
        slideshowList.setItems(FXCollections.observableArrayList());
        slideshowList.setItems(slideshows);

        slideshowList.getSelectionModel().select(selectedIndex);
    }

    private void onActiveSlideshowChanged(Observable observable) {
        Slideshow selected = slideshowList.getSelectionModel().getSelectedItem();
        selectedSlideshowProperty.setValue(selected);

        if (selected != null) {
            slideshowNameTextField.setText(selected.getName());

            if (selected.getDurationBetweenPhotos() <= 5) {
                shortDurationButton.setSelected(true);
            } else if (selected.getDurationBetweenPhotos() <= 10) {
                mediumDurationButton.setSelected(true);
            } else {
                longDurationButton.setSelected(true);
            }
        }
    }

    private void handleAddSlideshow(Event event) {
        Slideshow slideshow = new Slideshow(-1, NEW_SLIDESHOW_DEFAULT_NAME, 5.0);

        try {
            slideshow = slideshowService.create(slideshow);

            if (slideshowAddedCallback != null) {
                slideshowAddedCallback.accept(slideshow);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Erstellen der Diashow", "");
        }
    }

    private void handleDeleteSlideshow(Event event) {
        Slideshow slideshow = getSelected();

        if (slideshow == null) {
            return;
        }

        try {
            slideshowService.delete(slideshow);

            if (slideshowDeletedCallback != null) {
                slideshowDeletedCallback.accept(slideshow);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Löschen der Diashow", "");
        }
    }

    private void updateSelectedSlideshowName(Object observable) {
        Slideshow selected = getSelected();
        String name = slideshowNameTextField.getText();

        if (selected != null && !name.equals(selected.getName())) {
            LOGGER.debug("Setting name from {} to {}", selected.getName(), name);
            selected.setName(name);

            try {
                slideshowService.update(selected);
                refreshSlideshowList();
            } catch (ServiceException ex) {
                ErrorDialog.show(root, "Fehler beim Ändern der Slideshow", "");
            }
        }
    }

    private void updateSlideshowDuration(Object observable) {
        Slideshow selected = getSelected();
        double duration = getSelectedDuration();

        if (selected != null && !selected.getDurationBetweenPhotos().equals(duration)) {
            selected.setDurationBetweenPhotos(duration);

            try {
                slideshowService.update(selected);
            } catch (ServiceException ex) {
                ErrorDialog.show(root, "Fehler beim Ändern der Dauer", "Fehlermeldung: " + ex.getMessage());
            }
        }
    }

    private double getSelectedDuration() {
        if (shortDurationButton.isSelected()) {
            return 5.0;
        } else if (mediumDurationButton.isSelected()) {
            return 10.0;
        } else {
            return 15.0;
        }
    }

    private class SlideshowCellFactory implements Callback<ListView<Slideshow>, ListCell<Slideshow>> {
        @Override
        public ListCell<Slideshow> call(ListView<Slideshow> param) {
            return new SlideshowCell();
        }
    }

    private class SlideshowCell extends ListCell<Slideshow> {
        @Override
        protected void updateItem(Slideshow item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
            } else if (item != null) {
                setText(item.getName());
            }
        }
    }
}
