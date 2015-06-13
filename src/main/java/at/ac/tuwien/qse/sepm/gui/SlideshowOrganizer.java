package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class SlideshowOrganizer {

    @FXML
    private ListView<Slideshow> slideshowList;

    @Autowired
    private SlideshowService slideshowService;

    @FXML
    private void initialize() {
        slideshowList.setCellFactory(new SlideshowCellFactory());
        
        loadSlideshows();
    }

    private void loadSlideshows() {
        try {
            List<Slideshow> slideshows = slideshowService.getAllSlideshows();
            slideshowList.getItems().addAll(slideshows);
        } catch (ServiceException ex) {
            ErrorDialog.show(slideshowList, "Fehler beim Laden aller Slideshows", "Fehlermeldung: " + ex.getMessage());
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

            if (item != null) {
                setText(item.getName());
            }
        }
    }
}
