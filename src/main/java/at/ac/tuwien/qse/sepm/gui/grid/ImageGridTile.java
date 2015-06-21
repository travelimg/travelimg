package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.control.SmartImage;

public abstract class ImageGridTile extends Tile {

    private static final int IMG_SIZE = 200;

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

        image = new SmartImage();
        image.setPrefHeight(IMG_SIZE);
        image.setPrefWidth(IMG_SIZE);
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
