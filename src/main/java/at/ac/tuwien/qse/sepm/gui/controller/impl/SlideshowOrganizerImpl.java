package at.ac.tuwien.qse.sepm.gui.controller.impl;


import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.controller.SlideshowOrganizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.SlideshowServiceImpl;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


public class SlideshowOrganizerImpl implements SlideshowOrganizer {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private ListView<Slideshow> slideshowList;

    @FXML
    private BorderPane root;

    @FXML
    private Button presentButton;

    @FXML
    private Button updateButton;

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

    private ObjectProperty<Slideshow> selectedSlideshowProperty = new SimpleObjectProperty<>(null);


    public ObjectProperty<Slideshow> getSelectedSlideshowProperty() {
        return selectedSlideshowProperty;
    }

    public Slideshow getSelected() {
        return selectedSlideshowProperty.get();
    }

    public void setSlideshows(ObservableList<Slideshow> slideshows) {
        // only display real slideshows (no placeholder)
        FilteredList<Slideshow> filtered = slideshows.filtered(s -> s.getId() >= 0);
        slideshowList.setItems(filtered);
    }

    @Override
    public void setPresentAction(Runnable callback) {
        LOGGER.debug("setting present action");
        presentButton.setOnAction(event -> callback.run());

    }

    @FXML
    private void initialize() {

        slideshowList.setCellFactory(new SlideshowCellFactory());
        updateButton.setOnAction(this::updateSelectedSlideshowName);

        slideshowList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedSlideshowProperty.setValue(newValue);
            onActiveSlideshowChanged();
        });

        //Add Buttons to Tooggle Group
        shortDurationButton.setSelected(true);
        shortDurationButton.setToggleGroup(durationToggleGroup);
        mediumDurationButton.setToggleGroup(durationToggleGroup);
        longDurationButton.setToggleGroup(durationToggleGroup);
        
        durationToggleGroup.selectedToggleProperty().addListener(this::updateSlideshowDuration);
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

            if (item != null) {
                setText(item.getName());
            }
        }
    }


    private void updateSelectedSlideshowName(Event event) {
        if(slideshowNameTextField.getText().isEmpty())
            slideshowNameTextField.setText(getSelected().getName());
        else {
            Slideshow updatedSlideshow = new Slideshow();
            updatedSlideshow.setName(slideshowNameTextField.getText());
            updatedSlideshow.setDurationBetweenPhotos(getSelected().getDurationBetweenPhotos());
            updatedSlideshow.setSlides(getSelected().getSlides());
            updatedSlideshow.setId(getSelected().getId());
            try {
                slideshowService.update(updatedSlideshow);
                getSelected().setName(slideshowNameTextField.getText());
                updateSlideshowListView();
                slideshowNameTextField.clear();
            } catch (ServiceException e) {
                ErrorDialog.show(root, "Fehler beim Ändern der Slideshow", "Fehlermeldung: " + e.getMessage());
            }


        }


    }

    private void updateSlideshowListView() {
        ObservableList<Slideshow> slideshows = slideshowList.getItems();
        slideshowList.setItems(FXCollections.observableArrayList());
        slideshowList.setItems(slideshows);


    }

    private void onActiveSlideshowChanged(){
        slideshowNameTextField.setText(getSelected().getName());
    }

    private void updateSlideshowDuration(Object observable) {
        Slideshow selected = getSelected();
        if (selected != null) {
            selected.setDurationBetweenPhotos(getSelectedDuration());

            try {
                slideshowService.update(selected);
            } catch (ServiceException ex) {
                ErrorDialog.show(root, "Fehler beim Ändern der Dauer", "Fehlermeldung: " + ex.getMessage());
            }
        }
    }

    public double getSelectedDuration(){
        if (shortDurationButton.isSelected()) {
            return 5.0;
        } else if (mediumDurationButton.isSelected()) {
            return 10.0;
        } else {
            return 15.0;
        }
    }


}
