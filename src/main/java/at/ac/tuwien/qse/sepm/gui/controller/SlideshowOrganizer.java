package at.ac.tuwien.qse.sepm.gui.controller;


import at.ac.tuwien.qse.sepm.entities.Slideshow;

import java.util.function.Consumer;

public interface SlideshowOrganizer {

    void setAddAction(Consumer<Slideshow> callback);
    void setDeleteAction(Consumer<Slideshow> callback);
    void setPresentAction(Runnable callback);

}
