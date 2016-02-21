package at.ac.tuwien.qse.sepm.gui.controller.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.controller.GridView;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.Menu;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.*;
import at.ac.tuwien.qse.sepm.gui.grid.PaginatedImageGrid;
import at.ac.tuwien.qse.sepm.gui.util.BufferedBatchOperation;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.PhotoServiceImpl;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class GridViewImpl implements GridView {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private PhotoService photoService;
    @Autowired
    private FlickrService flickrService;
    @Autowired
    private ExportService exportService;
    @Autowired
    private PhotographerService photographerService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private Organizer organizer;
    @Autowired
    private Inspector<Photo> inspector;
    @Autowired
    private Menu menu;
    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private ExifService exifService;

    @Autowired
    private WorkspaceService workspaceService;

    @FXML
    private StackPane root;

    @FXML
    private BorderPane content;

    @FXML
    private Node folderDropTarget;

    private PaginatedImageGrid grid;

    private BufferedBatchOperation<Photo> addOperation = null;
    private BufferedBatchOperation<Photo> updateOperation = null;
    private BufferedBatchOperation<Path> deleteOperation = null;

    @Autowired public void setScheduler(ScheduledExecutorService scheduler) {
        addOperation = new BufferedBatchOperation<>(this::handleAddPhotos, scheduler);
        updateOperation = new BufferedBatchOperation<>(this::handleUpdatePhotos, scheduler);
        deleteOperation = new BufferedBatchOperation<>(this::handleDeletePhotos, scheduler);
    }

    @FXML
    private void initialize() {
        LOGGER.debug("initializing");

        this.grid = new PaginatedImageGrid(menu);
        content.setCenter(grid);
        menu.addListener(new MenuListener());
        organizer.setFilterChangeAction(this::handleFilterChange);

        // Selected photos are shown in the inspector.
        grid.setSelectionChangeAction(inspector::setEntities);


        // CTRL+A select all photos.
        root.setOnMouseClicked((event) -> root.requestFocus());
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.A) {
                grid.selectAll();
            }
        });

        // Updated photos that no longer match the filter are removed from the grid.
        inspector.setUpdateHandler(() -> {
            Collection<Photo> photos = inspector.getEntities();

            List<Photo> toRemove = new ArrayList<>(photos.size());
            List<Photo> toUpdate = new ArrayList<>(photos.size());

            photos.forEach(p -> {
                if (!organizer.accept(p)) {
                    organizer.remove(p);
                    toRemove.add(p);
                } else {
                    toUpdate.add(p);
                }
            });

            grid.removePhotos(toRemove);
            grid.updatePhotos(toUpdate);
        });

        // Apply the initial filter.
        handleFilterChange();

        // subscribe to photo events
        photoService.subscribeCreate(this::handlePhotoCreated);
        photoService.subscribeUpdate(this::handlePhotoUpdated);
        photoService.subscribeDelete(this::handlePhotoDeleted);

        root.setOnDragEntered(this::handleDragEntered);
        root.setOnDragOver(this::handleDragOver);
        root.setOnDragDropped(this::handleDragDropped);
        root.setOnDragExited(this::handleDragExited);

        try {
            if (workspaceService.getDirectories().isEmpty()) {
                folderDropTarget.setVisible(true);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden der Bildordner", "");
        }
    }

    private void handleAddPhotos(List<Photo> photos) {
        LOGGER.debug("adding {} photos to grid", photos.size());
        Platform.runLater(() -> grid.addPhotos(photos));
    }

    private void handleUpdatePhotos(List<Photo> photos) {
        LOGGER.debug("updating {} photos in grid", photos.size());
        Platform.runLater(() -> {
            grid.updatePhotos(photos);

            // update the inspector selection
            Collection<Photo> entities = inspector.getEntities();

            boolean updated = false;
            for (Photo photo : photos) {
                Optional<Photo> entity = entities.stream().filter(p -> p.getId().equals(photo.getId())).findFirst();
                if (entity.isPresent()) {
                    entities.remove(entity.get());
                    entities.add(photo);
                    updated = true;
                }
            }

            if (updated) {
                inspector.setEntities(entities);
            }
        });
    }

    private void handleDeletePhotos(List<Path> paths) {
        LOGGER.debug("deleting {} photos in grid", paths.size());

        Platform.runLater(() -> {
            List<Photo> photos = grid.getPhotos().stream()
                    .filter(p -> paths.contains(p.getFile()))
                    .collect(Collectors.toList());

            grid.removePhotos(photos);
        });
    }

    private void handlePhotoCreated(Photo photo) {
        if (organizer.accept(photo)) {
            addOperation.add(photo);
        }
    }

    private void handlePhotoUpdated(Photo photo) {
        if (organizer.accept(photo)) {
            updateOperation.add(photo);
        }
    }

    private void handlePhotoDeleted(Path file) {
        deleteOperation.add(file);
    }

    private void handleFilterChange() {
        reloadImages();
    }

    private void reloadImages() {
        organizer.reset();
        try {
            grid.setPhotos(photoService.getAllPhotos(organizer::accept).stream()
                            .sorted((p1, p2) -> p2.getData().getDatetime().compareTo(p1.getData().getDatetime()))
                            .collect(Collectors.toList()));
        } catch (ServiceException ex) {
            LOGGER.error("failed loading fotos", ex);
            ErrorDialog.show(root, "Laden von Fotos fehlgeschlagen",
                    "Fehlermeldung: " + ex.getMessage());
        }
    }

    private void handleDragEntered(DragEvent event) {
        LOGGER.debug("drag entered");
        folderDropTarget.setVisible(true);
        event.consume();
    }

    private void handleDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.LINK);
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        LOGGER.debug("drag dropped");

        Dragboard dragboard = event.getDragboard();
        boolean success = dragboard.hasFiles();
        if (success) {
            dragboard.getFiles().forEach(f -> LOGGER.debug("dropped file {}", f));
            for (File file : dragboard.getFiles()) {
                try {
                    workspaceService.addDirectory(file.toPath());
                } catch (ServiceException ex) {
                    LOGGER.error("Couldn't add directory {}");
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void handleDragExited(DragEvent event) {
        LOGGER.debug("drag exited");
        folderDropTarget.setVisible(false);
        event.consume();
    }

    private class MenuListener implements Menu.Listener {

        @Override public void onPresent(Menu sender) {

            FullscreenWindow fullscreen = new FullscreenWindow(photoService);

            Collection<Photo> selected = grid.getSelected();

            Photo start = grid.getActivePhoto();
            if (selected.size() == 1) {
                start = selected.iterator().next();
            }

            fullscreen.present(grid.getPhotos(), start);
        }

        @Override public void onFlickr(Menu sender) {
            FlickrDialog flickrDialog = new FlickrDialog(root, "Flickr Import", flickrService, exifService, clusterService, photoService, ioHandler, sender);
            flickrDialog.showAndWait();
        }

        @Override public void onJourney(Menu sender) {
            JourneyDialog dialog = new JourneyDialog(root, clusterService);
            dialog.showForResult();
        }

        @Override public void onDelete(Menu sender) {
            Collection<Photo> selection = grid.getSelected();
            if (selection.isEmpty()) {
                return;
            }

            DeleteDialog deleteDialog = new DeleteDialog(root, selection.size());
            Optional<Boolean> confirmed = deleteDialog.showForResult();
            if (!confirmed.isPresent() || !confirmed.get()) return;

            try {
                photoService.deletePhotos(selection);
            } catch (ServiceException ex) {
                LOGGER.error("failed deleting photos", ex);
                ErrorDialog.show(root, "Fehler beim Löschen", "Die ausgewählten Fotos konnten nicht gelöscht werden.");
            }

            grid.removePhotos(selection);
        }

        @Override public void onExport(Menu sender) {
            Collection<Photo> selection = grid.getSelected();

            ExportDialog dialog = new ExportDialog(root, exportService, selection.size());

            Optional<String> destinationPath = dialog.showForResult();
            if (!destinationPath.isPresent()) return;

            exportService.exportPhotos(selection, destinationPath.get(), photo -> {
            }, exception -> ErrorDialog
                    .show(root, "Fehler beim Export", "Fehlermeldung: " + exception.getMessage()));
        }
    }
}
