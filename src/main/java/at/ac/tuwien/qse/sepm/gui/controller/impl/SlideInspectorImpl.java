package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.Slide;

public abstract class SlideInspectorImpl<S extends Slide> {

    private S slide = null;
    private Runnable updateHandler;

    public S getSlide() {
        return slide;
    }

    public void setSlide(S slide) {
        this.slide = slide;
    }

    public void setUpdateHandler(Runnable updateHandler) {
        this.updateHandler = updateHandler;
    }

    protected void onUpdate() {
        if (updateHandler != null) {
            updateHandler.run();
        }
    }
}
