package at.ac.tuwien.qse.sepm.gui.slide;

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

import at.ac.tuwien.qse.sepm.entities.*;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

public class SlideView extends StackPane {

    public static SlideView of(Slide slide, int width, int height) {
        if (slide instanceof PhotoSlide) {
            return new PhotoSlideView((PhotoSlide)slide, width, height);
        } else if (slide instanceof MapSlide) {
            return new MapSlideView((MapSlide)slide);
        } else if (slide instanceof TitleSlide) {
            return new TitleSlideView((TitleSlide)slide);
        }

        return null;
    }

    protected Node createCaptionBox(String text) {
        Group overlay = new Group();
        HBox overlayBox = new HBox();
        Label caption = new Label(text);
        caption.setStyle("rgba(0,0,0,255)");
        caption.setTextFill(Paint.valueOf("white"));
        caption.setStyle("-fx-text-fill: white !important;");
        caption.setStyle("-fx-font-weight: bold");
        caption.setStyle("-fx-text-alignment: center");
        caption.setStyle("-fx-font-size: 70px");
        //caption.setBackground(new Background(new BackgroundFill(Paint.valueOf("white"),null,null)));
        overlayBox.getStyleClass().add("caption-overlay");
        overlayBox.getChildren().add(caption);
        overlay.getChildren().add(overlayBox);

        return overlay;
    }
}
