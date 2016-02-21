package at.ac.tuwien.qse.sepm.gui.control;

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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Widget for one widget in the image grid. Can either be in a selected or an unselected state.
 */
public class FlickrImageTile extends StackPane {

    private static final Logger logger = LogManager.getLogger();
    private static final String tmpDir = System.getProperty("java.io.tmpdir");
    private final BorderPane overlay = new BorderPane();
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    private com.flickr4java.flickr.photos.Photo flickrPhoto;

    public FlickrImageTile(com.flickr4java.flickr.photos.Photo flickrPhoto) {
        super();
        this.flickrPhoto = flickrPhoto;
        Image image = null;
        try {
            FileInputStream fis = new FileInputStream(new File(
                    Paths.get(tmpDir, flickrPhoto.getId() + "." + flickrPhoto.getOriginalFormat()).toString()));
            image = new Image(fis, 150, 0, true, true);
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.error("Could not find photo", ex);
            return;
        } catch (IOException e) {
            logger.error("Could not close fileinputstream", e);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        getChildren().add(imageView);

        setAlignment(overlay, Pos.BOTTOM_CENTER);
        FontAwesomeIconView checkIcon = new FontAwesomeIconView();
        checkIcon.setGlyphName("CHECK");
        checkIcon.setStyle("-fx-fill: white");
        overlay.setAlignment(checkIcon, Pos.CENTER_RIGHT);
        overlay.setStyle("-fx-background-color: -tmg-secondary; -fx-max-height: 20px;");
        overlay.setBottom(checkIcon);
        getChildren().add(overlay);
        overlay.setVisible(false);
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                if(!getSelectedProperty().getValue()){
                    select();
                }
                else{
                    deselect();
                }
            }
        });
    }

    public com.flickr4java.flickr.photos.Photo getFlickrPhoto() {
        return flickrPhoto;
    }

    public void select() {
        selected.setValue(true);
        overlay.setVisible(true);
        setStyle("-fx-border-color: -tmg-secondary");
    }

    public void deselect() {
        selected.setValue(false);
        overlay.setVisible(false);
        setStyle("-fx-border-color: none");
    }

    /**
     * Property which represents if this tile is currently selected or not.
     *
     * @return The selected property.
     */
    public BooleanProperty getSelectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.getValue();
    }

}
