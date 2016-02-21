package at.ac.tuwien.qse.sepm.gui.grid;

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
import at.ac.tuwien.qse.sepm.gui.control.SmartImage;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;

public abstract class ImageGridTile extends Tile {

    private static final ImageSize size = ImageSize.MEDIUM;

    private Photo photo = null;
    private SmartImage image = null;

    public ImageGridTile() {
        getStyleClass().add("image-tile");
    }

    /**
     * Set the photo this tile represents.
     *
     * @param photo photo this tile represents, or null
     */
    public void setPhoto(Photo photo) {
        this.photo = photo;

        if (image != null) {
            getChildren().remove(image);
        }

        if (photo == null) {
            return;
        }

        image = new SmartImage(size);
        image.setPrefHeight(size.pixels());
        image.setPrefWidth(size.pixels());
        image.setImage(photo.getFile());

        getChildren().add(0, image);
    }

    /**
     * @return photo this tile represents, or null
     */
    public Photo getPhoto() {
        return photo;
    }
}
