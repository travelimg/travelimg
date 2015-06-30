package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.PresentationWindow;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.controller.SlideshowView;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.grid.SlideGrid;
import at.ac.tuwien.qse.sepm.gui.slide.SlideCallback;
import at.ac.tuwien.qse.sepm.gui.util.BufferedBatchOperation;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class SlideshowViewImpl implements SlideshowView {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NEW_SLIDESHOW_PROMPT = "Zu neuer Präsentation hinzufügen";
    private static final String NEW_SLIDESHOW_NAME = "Neue Präsentation";
    private static final int NEW_SLIDESHOW_MARKER_ID = -1;

    @Autowired
    private SlideService slideService;
    @Autowired
    private SlideshowService slideShowService;
    @Autowired
    private PhotoService photoService;

    @Autowired
    private SlideInspectorImpl<PhotoSlide> photoSlideInspector;
    @Autowired
    private SlideInspectorImpl<TitleSlide> titleSlideInspector;
    @Autowired
    private SlideInspectorImpl<MapSlide> mapSlideInspector;
    @FXML
    private InspectorPane photoSlideInspectorPane;
    @FXML
    private InspectorPane titleSlideInspectorPane;
    @FXML
    private InspectorPane mapSlideInspectorPane;
    @FXML
    private BorderPane root;
    @FXML
    private SlideGrid grid;

    @Autowired
    private SlideshowOrganizerImpl slideshowOrganizer;
    @Autowired
    private SlideshowService slideshowService;

    private ObservableList<Slideshow> slideshows = FXCollections.observableArrayList();
    private BufferedBatchOperation<Path> deletedOperation;

    @Autowired
    public void setScheduler(ScheduledExecutorService scheduler) {
        deletedOperation = new BufferedBatchOperation<>(this::handleDeletedPhotos, scheduler);
    }

    @FXML
    private void initialize() {
        grid.setSlideAddedCallback(new SlideAddedCallback());
        grid.setSlideChangedCallback(new SlideChangedCallback());
        grid.setSlideSelectedCallback(new SlideSelectedCallback());

        slideshowOrganizer.setSlideshows(slideshows);
        slideshowOrganizer.getSelectedSlideshowProperty().addListener((observable, oldValue, newValue) -> {
            grid.setSlideshow(newValue);
        });

        loadAllSlideshows();

        slideshowOrganizer.setAddAction((slideshow) -> {
            slideshows.remove(slideshows.size() - 1); // remove placeholder
            slideshows.add(slideshow); // add created slideshow
            slideshows.add(createNewSlideshowPlaceholder()); // re-add placeholder
            slideshowOrganizer.setSelected(slideshows.size() - 2);
        });

        slideshowOrganizer.setDeleteAction(this::handleDeleteSlideshow);

        slideshowOrganizer.setPresentAction(() -> {
            Slideshow selected = slideshowOrganizer.getSelected();
            PresentationWindow presentationWindow = new PresentationWindow(selected);
            presentationWindow.present();
        });

        photoSlideInspector.setUpdateHandler(() -> grid.setSlideshow(slideshowOrganizer.getSelected()));
        mapSlideInspector.setUpdateHandler(() -> grid.setSlideshow(slideshowOrganizer.getSelected()));
        titleSlideInspector.setUpdateHandler(() -> grid.setSlideshow(slideshowOrganizer.getSelected()));

        photoSlideInspector.setDeleteHandler(slide -> {
            slideshowOrganizer.getSelected().getPhotoSlides().remove(slide);
            grid.setSlideshow(slideshowOrganizer.getSelected());
        });

        mapSlideInspector.setDeleteHandler(slide -> {
            slideshowOrganizer.getSelected().getMapSlides().remove(slide);
            grid.setSlideshow(slideshowOrganizer.getSelected());
        });

        titleSlideInspector.setDeleteHandler(slide -> {
            slideshowOrganizer.getSelected().getTitleSlides().remove(slide);
            grid.setSlideshow(slideshowOrganizer.getSelected());
        });

        photoService.subscribeDelete(photo -> {
            deletedOperation.add(photo);
        });
    }

    @Override
    public ObservableList<Slideshow> getSlideshows() {
        return slideshows;
    }

    @Override
    public void addPhotosToSlideshow(List<Photo> photos, Slideshow slideshow) {
        try {
            if (slideshow.getId() == NEW_SLIDESHOW_MARKER_ID) {
                // user added photos to new slideshow which does not exist yet. create it
                slideshow.setName(NEW_SLIDESHOW_NAME);
                slideshow = slideshowService.create(slideshow);

                // remove placeholder, add new slideshow and add new placeholder
                slideshows.remove(slideshows.size() - 1);
                slideshows.add(slideshow);
                slideshows.add(createNewSlideshowPlaceholder());

                slideshowOrganizer.setSlideshows(null);
                slideshowOrganizer.setSlideshows(slideshows);
            }

            slideshowService.addPhotosToSlideshow(photos, slideshow);

            // add the photos to the grid if the slideshow is currently being displayed
            Slideshow selected = slideshowOrganizer.getSelected();
            if (selected != null && selected.getId().equals(slideshow.getId())) {
                grid.setSlideshow(slideshow);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Hinzufügen zur Slideshow", "Fehlermeldung: " + ex.getMessage());
        }
    }

    private void loadAllSlideshows() {
        try {
            int selectedIndex = slideshows.indexOf(slideshowOrganizer.getSelected());
            slideshows.clear();
            slideshows.addAll(slideShowService.getAllSlideshows());
            slideshows.add(createNewSlideshowPlaceholder()); // represents a new slideshow which will be created if the user makes use of it

            if (selectedIndex >= 0) {
                slideshowOrganizer.setSelected(selectedIndex);
            } else if (slideshows.size() > 0) {
                slideshowOrganizer.setSelected(0);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden aller Slideshows", "Fehlermeldung: " + ex.getMessage());
        }
    }

    private void handleDeletedPhotos(List<Path> paths) {
        Platform.runLater(() -> {
            loadAllSlideshows();
            refreshGrid();
        });
    }

    private void handleDeleteSlideshow(Slideshow slideshow) {
        slideshows.remove(slideshow);

        try {
            slideShowService.delete(slideshow);
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Löschen der Diashow", "");
        }
    }

    private Slideshow createNewSlideshowPlaceholder() {
        double durationBetweenPhotos = 5; // TODO
        return new Slideshow(NEW_SLIDESHOW_MARKER_ID, NEW_SLIDESHOW_PROMPT, durationBetweenPhotos);
    }

    private void refreshGrid() {
        grid.setSlideshow(slideshowOrganizer.getSelected());
    }

    private class SlideSelectedCallback implements SlideCallback<Void> {
        @Override
        public void handle(PhotoSlide slide) {
            photoSlideInspector.setSlide(slide);

            photoSlideInspectorPane.setCount(1);
            photoSlideInspectorPane.setVisible(true);
            mapSlideInspectorPane.setVisible(false);
            titleSlideInspectorPane.setVisible(false);
        }

        @Override
        public void handle(MapSlide slide) {
            mapSlideInspector.setSlide(slide);

            mapSlideInspectorPane.setCount(1);
            mapSlideInspectorPane.setVisible(true);
            photoSlideInspectorPane.setVisible(false);
            titleSlideInspectorPane.setVisible(false);
        }

        @Override
        public void handle(TitleSlide slide) {
            titleSlideInspector.setSlide(slide);

            titleSlideInspectorPane.setCount(1);
            titleSlideInspectorPane.setVisible(true);
            photoSlideInspectorPane.setVisible(false);
            mapSlideInspectorPane.setVisible(false);
        }
    }

    private class SlideAddedCallback implements SlideCallback<Integer> {

        @Override
        public void handle(MapSlide slide, Integer position) {
            Slideshow selected = slideshowOrganizer.getSelected();

            if (selected == null) {
                return;
            }

            slide.setOrder(position + 1);
            slide.setSlideshowId(selected.getId());

            try {
                slide = slideService.create(slide);
            } catch (ServiceException ex) {
                ErrorDialog.show(root, "Fehler beim Erstellen der Slide", "");
                return;
            }

            updateOrderForOtherSlides(selected, position);
            selected.getMapSlides().add(slide);
            refreshGrid();
        }

        @Override
        public void handle(TitleSlide slide, Integer position) {
            Slideshow selected = slideshowOrganizer.getSelected();

            if (selected == null) {
                return;
            }

            slide.setOrder(position + 1);
            slide.setSlideshowId(selected.getId());

            try {
                slide = slideService.create(slide);
            } catch (ServiceException ex) {
                ErrorDialog.show(root, "Fehler beim Erstellen der Slide", "");
                return;
            }

            updateOrderForOtherSlides(selected, position);
            selected.getTitleSlides().add(slide);
            refreshGrid();
        }

        private void updateOrderForOtherSlides(Slideshow slideshow, int insertPosition) {

            for (PhotoSlide slide : slideshow.getPhotoSlides()) {
                if (slide.getOrder() > insertPosition) {
                    slide.setOrder(slide.getOrder() + 1);

                    try {
                        slideService.update(slide);
                    } catch (ServiceException ex) {
                        ErrorDialog.show(root, "Fehler beim Setzen der neuen Reihenfolge", "");
                    }
                }
            }

            for (MapSlide slide : slideshow.getMapSlides()) {
                if (slide.getOrder() > insertPosition) {
                    slide.setOrder(slide.getOrder() + 1);

                    try {
                        slideService.update(slide);
                    } catch (ServiceException ex) {
                        ErrorDialog.show(root, "Fehler beim Setzen der neuen Reihenfolge", "");
                    }
                }
            }

            for (TitleSlide slide : slideshow.getTitleSlides()) {
                if (slide.getOrder() > insertPosition) {
                    slide.setOrder(slide.getOrder() + 1);

                    try {
                        slideService.update(slide);
                    } catch (ServiceException ex) {
                        ErrorDialog.show(root, "Fehler beim Setzen der neuen Reihenfolge", "");
                    }
                }
            }
        }
    }

    private class SlideChangedCallback implements SlideCallback<Void> {
        @Override
        public void handle(PhotoSlide slide) {
            try {
                slideService.update(slide);
            } catch (ServiceException ex) {
                ErrorDialog.show(root, "Fehler beim Ändern der Slides", "");
            }
        }

        @Override
        public void handle(MapSlide slide) {
            try {
                slideService.update(slide);
            } catch (ServiceException ex) {
                ErrorDialog.show(root, "Fehler beim Ändern der Slides", "");
            }
        }

        @Override
        public void handle(TitleSlide slide) {
            try {
                slideService.update(slide);
            } catch (ServiceException ex) {
                ErrorDialog.show(root, "Fehler beim Ändern der Slides", "");
            }
        }
    }
}
