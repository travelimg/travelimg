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

import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.nio.file.Path;

public class SmartImage extends StackPane {

    private static final ImageCache cache = new ImageCache();

    private final ProgressIndicator progress = new ProgressIndicator();
    private final ImageView imageView = new ImageView();

    private Image image = null;
    private ImageSize size;

    public SmartImage(ImageSize size) {
        this.size = size;

        getStyleClass().add("smart-image");

        setMargin(progress, new Insets(16));
        setPadding(new Insets(4));

        heightProperty().addListener(this::handleSizeChange);
        widthProperty().addListener(this::handleSizeChange);

        getChildren().add(progress);
        getChildren().add(imageView);
    }

    public void setImage(Path path) {
        indicateLoading();

        if (path == null) {
            return;
        }

        image = cache.get(path, size);
        imageView.setImage(image);

        // NOTE: Image may be loaded already.
        if (image.getProgress() == 1.0) {
            indicateLoaded();
            return;
        }

        image.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                indicateLoaded();
            }
        });
    }

    public void setPreserveRatio(boolean preserve) {
        imageView.setPreserveRatio(preserve);
    }

    public void fitToSize(double width, double height) {
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    public void cancel() {
        if (image == null) return;
        image.cancel();
    }

    private void indicateLoading() {
        getStyleClass().add("loading");
        progress.setVisible(true);
        imageView.setVisible(false);
    }

    private void indicateLoaded() {
        getStyleClass().removeAll("loading");
        progress.setVisible(false);
        imageView.setVisible(true);
    }

    private void handleSizeChange(Object observable) {
        imageView.setFitWidth(getWidth());
        imageView.setFitHeight(getHeight());
    }
}
