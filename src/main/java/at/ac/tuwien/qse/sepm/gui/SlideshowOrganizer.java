package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.impl.OrganizerImpl;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class SlideshowOrganizer {

    private static final Logger LOGGER = LogManager.getLogger(OrganizerImpl.class);

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
