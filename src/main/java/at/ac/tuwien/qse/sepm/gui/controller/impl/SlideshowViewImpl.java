package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.PresentationWindow;
import at.ac.tuwien.qse.sepm.gui.controller.SlideshowView;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.grid.SlideGrid;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SlideshowViewImpl implements SlideshowView {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NEW_SLIDESHOW_PROMPT = "Zu neuer Präsentation hinzufügen";
    private static final String NEW_SLIDESHOW_NAME = "Neue Präsentation";
    private static final int NEW_SLIDESHOW_MARKER_ID = -1;

    @Autowired private SlideService slideService;
    @Autowired private SlideshowService slideShowService;
    @Autowired
    private ImageCache imageCache;


    @FXML private BorderPane root;
    @FXML private ScrollPane gridContainer;

    @Autowired
    private SlideshowOrganizerImpl slideshowOrganizer;
    @Autowired
    private SlideshowService slideshowService;

    private SlideGrid grid = new SlideGrid();

    private ObservableList<Slideshow> slideshows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        gridContainer.setContent(grid);

        grid.setSlideChangedCallback(this::handleSlideChanged);
        grid.setSlideAddedCallback(this::handleSlideAdded);


        slideshowOrganizer.setSlideshows(slideshows);
        slideshowOrganizer.getSelectedSlideshowProperty().addListener((observable, oldValue, newValue) -> {
            //grid.setSlideshow(newValue);
        });

        loadAllSlideshows();

        slideshowOrganizer.setPresentAction(() -> {
            Slideshow selected = slideshowOrganizer.getSelected();
            PresentationWindow presentationWindow = new PresentationWindow(selected);
            presentationWindow.present();
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
                Slideshow placeholder = slideshows.remove(slideshows.size() - 1);
                slideshows.add(slideshow);
                slideshows.add(createNewSlideshowPlaceholder());
            }

            slideshowService.addPhotosToSlideshow(photos, slideshow);

            // add the photos to the grid if the slideshow is currently being displayed
            Slideshow selected = slideshowOrganizer.getSelected();
            if (selected != null && selected.getId().equals(slideshow.getId())) {
                //grid.setSlideshow(slideshow);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Hinzufügen zur Slideshow", "Fehlermeldung: " + ex.getMessage());
        }
    }

    private void updateSlideshowDuration(Object observable) {
        Slideshow selected = slideshowOrganizer.getSelected();
        if (selected != null) {
            selected.setDurationBetweenPhotos(slideshowOrganizer.getSelectedDuration());
            LOGGER.debug(slideshowOrganizer.getSelectedDuration()+"Aktuelle Duration!");

            try {
                slideshowService.update(selected);
            } catch (ServiceException ex) {
                ErrorDialog.show(root, "Fehler beim Ändern der Dauer", "Fehlermeldung: " + ex.getMessage());
            }
        }
    }
/* -- TODO: Delete Methods
    private void createSlideshow() {
        try {
            Slideshow slideshow = new Slideshow();

            if (tf_slideName.getText().isEmpty()) {
                LOGGER.debug("Bitte geben Sie einen Namen für die Slideshow ein!");//TODO: Show an InfoBox
            } else {
                slideshow.setId(1);
                slideshow.setName(tf_slideName.getText());
                slideshow.setDurationBetweenPhotos(slideshowOrganizer.getSelectedDuration());

                slideShowService.create(slideshow);
                cb_getSlideshows.getItems().add(tf_slideName.getText());
                tf_slideName.clear();
                LOGGER.info("Slideshow wurde korrekt angelegt!");
            }


        } catch (ServiceException e) {
            e.printStackTrace();
        }

    }

    private void handlesetShowSlides(Event event) {
        createSlideshow();

    }*/

    private void handleSlideChanged(Slide slide) {
        try {
            slideService.update(slide);
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern der Slides", "Fehlermeldung: " + ex.getMessage());
        }

        // sort slides in the slideshow to which the slide belongs
        Optional<Slideshow> slideshow = slideshows.stream()
                .filter(s -> s.getId().equals(slide.getSlideshowId()))
                .findFirst();

        if (slideshow.isPresent()) {
            List<Slide> sorted = slideshow.get().getSlides().stream()
                    .sorted((s1, s2) -> s1.getOrder().compareTo(s2.getOrder()))
                    .collect(Collectors.toList());
            slideshow.get().setSlides(sorted);
        }
    }

    private void handleSlideAdded(Slide slide, Integer position) {
        Slideshow selected = slideshowOrganizer.getSelected();

        if (selected == null) {
            return;
        }

        slide.setOrder(position);
        slide.setSlideshowId(selected.getId());

        try {
            slideService.create(slide);
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Erstellen der Slide", "");
            return;
        }

        int i = 0;
        for (Slide s : selected.getSlides()) {
            if (i > position) {
                s.setOrder(s.getOrder() + 1);

                try {
                    slideService.update(s);
                } catch (ServiceException ex) {
                    ErrorDialog.show(root, "Fehler beim Setzen der neuen Reihenfolge", "");
                }
            }

            i++;
        }
    }

    private void loadAllSlideshows() {
        try {
            slideshows.clear();
            slideshows.addAll(slideShowService.getAllSlideshows());
            slideshows.add(createNewSlideshowPlaceholder()); // represents a new slideshow which will be created if the user makes use of it
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden aller Slideshows", "Fehlermeldung: " + ex.getMessage());
        }
    }

    private Slideshow createNewSlideshowPlaceholder() {
        double durationBetweenPhotos = 5; // TODO
        List<Slide> slides = new ArrayList<>();

        return new Slideshow(NEW_SLIDESHOW_MARKER_ID, NEW_SLIDESHOW_PROMPT, durationBetweenPhotos, slides);
    }



}
