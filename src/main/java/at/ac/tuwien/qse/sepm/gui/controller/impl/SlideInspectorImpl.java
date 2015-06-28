package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.Slide;

import java.util.function.Consumer;

public abstract class SlideInspectorImpl<S extends Slide> {

    private S slide = null;
    private Runnable updateHandler;
    private Consumer<S> deleteHandler;

    public S getSlide() {
        return slide;
    }

    public void setSlide(S slide) {
        this.slide = slide;
    }

    public void setUpdateHandler(Runnable updateHandler) {
        this.updateHandler = updateHandler;
    }
    public void setDeleteHandler(Consumer<S> deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    protected void onUpdate() {
        if (updateHandler != null) {
            updateHandler.run();
        }
    }

    protected void onDelete(S slide) {
        if (deleteHandler != null) {
            deleteHandler.accept(slide);
        }
    }
}
