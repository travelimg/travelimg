package at.ac.tuwien.qse.sepm.gui.controller.impl;


import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.controller.SlideshowOrganizer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SlideshowOrganizerImpl implements SlideshowOrganizer {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private ListView<Slideshow> slideshowList;

    @FXML
    private Button presentButton;



    private ObjectProperty<Slideshow> selectedSlideshowProperty = new SimpleObjectProperty<>(null);


    public ObjectProperty<Slideshow> getSelectedSlideshowProperty() {
        return selectedSlideshowProperty;
    }

    public Slideshow getSelected() {
        return selectedSlideshowProperty.get();
    }

    public void setSlideshows(ObservableList<Slideshow> slideshows) {
        slideshowList.setItems(slideshows);
    }

    @Override
    public void setPresentAction(Runnable callback) {
        LOGGER.debug("setting present action");
        presentButton.setOnAction(event -> callback.run());
    }

    @FXML
    private void initialize() {

        slideshowList.setCellFactory(new SlideshowCellFactory());
        slideshowList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedSlideshowProperty.setValue(newValue);
        });

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



}
