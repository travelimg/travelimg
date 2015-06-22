package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.PresentationWindow;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

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
    private ImageCache imageCache;

    @Autowired
    private Inspector<PhotoSlide> photoSlideInspector;
    @Autowired
    private Inspector<TitleSlide> titleSlideInspector;
    @Autowired
    private Inspector<MapSlide> mapSlideInspector;
    @FXML
    private InspectorPane photoSlideInspectorPane;
    @FXML
    private InspectorPane titleSlideInspectorPane;
    @FXML
    private InspectorPane mapSlideInspectorPane;

    @FXML
    private BorderPane root;
    @FXML
    private ScrollPane gridContainer;

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

        grid.setSelectionChangeCallback(() -> {
            setInspectorEntities(grid.getSelected());
        });

        slideshowOrganizer.setSlideshows(slideshows);
        slideshowOrganizer.getSelectedSlideshowProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                grid.setSlides(newValue.getSlides());
            } else {
                grid.setSlides(FXCollections.observableArrayList());
            }
        });

        loadAllSlideshows();

        slideshowOrganizer.setAddAction((slideshow) -> {
            slideshows.remove(slideshows.size() - 1); // remove placeholder
            slideshows.add(slideshow); // add created slideshow
            slideshows.add(createNewSlideshowPlaceholder()); // re-add placeholder
            slideshowOrganizer.setSlideshows(slideshows);
        });

        slideshowOrganizer.setPresentAction(() -> {
            Slideshow selected = slideshowOrganizer.getSelected();
            PresentationWindow presentationWindow = new PresentationWindow(selected);
            presentationWindow.present();
        });

        photoSlideInspector.setUpdateHandler(() -> grid.setSlides(slideshowOrganizer.getSelected().getSlides()));
        mapSlideInspector.setUpdateHandler(() -> grid.setSlides(slideshowOrganizer.getSelected().getSlides()));
        titleSlideInspector.setUpdateHandler(() -> grid.setSlides(slideshowOrganizer.getSelected().getSlides()));
    }

    private void setInspectorEntities(Set<Slide> slides) {
        photoSlideInspector.setEntities(slides.stream()
                .filter(s -> s instanceof PhotoSlide)
                .map(s -> (PhotoSlide) s)
                .collect(Collectors.toList())
        );

        photoSlideInspectorPane.setVisible(!photoSlideInspector.getEntities().isEmpty());
        photoSlideInspectorPane.setManaged(!photoSlideInspector.getEntities().isEmpty());

        mapSlideInspector.setEntities(slides.stream()
                        .filter(s -> s instanceof MapSlide)
                        .map(s -> (MapSlide)s)
                        .collect(Collectors.toList())
        );

        mapSlideInspectorPane.setVisible(!mapSlideInspector.getEntities().isEmpty());
        mapSlideInspectorPane.setManaged(!mapSlideInspector.getEntities().isEmpty());

        titleSlideInspector.setEntities(slides.stream()
                        .filter(s -> s instanceof TitleSlide)
                        .map(s -> (TitleSlide)s)
                        .collect(Collectors.toList())
        );

        titleSlideInspectorPane.setVisible(!titleSlideInspector.getEntities().isEmpty());
        titleSlideInspectorPane.setManaged(!titleSlideInspector.getEntities().isEmpty());
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
            }

            slideshowService.addPhotosToSlideshow(photos, slideshow);

            // add the photos to the grid if the slideshow is currently being displayed
            Slideshow selected = slideshowOrganizer.getSelected();
            if (selected != null && selected.getId().equals(slideshow.getId())) {
                grid.setSlides(slideshow.getSlides());
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Hinzufügen zur Slideshow", "Fehlermeldung: " + ex.getMessage());
        }
    }

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

        slide.setOrder(position + 1);
        slide.setSlideshowId(selected.getId());

        try {
            slide = slideService.create(slide);
            selected.getSlides().add(position, slide);
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

        grid.setSlides(selected.getSlides());
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
