package at.ac.tuwien.qse.sepm.gui.grid;

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
