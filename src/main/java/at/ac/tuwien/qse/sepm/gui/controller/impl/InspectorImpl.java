package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class InspectorImpl<E> implements Inspector<E> {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private InspectorPane root;

    private final Collection<E> entities = new LinkedList<>();
    private Runnable updateHandler;

    @Override public Collection<E> getEntities() {
        LOGGER.debug("retrieving {} entities", entities.size());
        return new ArrayList<>(entities);
    }

    @Override public void setEntities(Collection<E> entities) {
        if (entities == null) entities = new ArrayList<>(0);
        LOGGER.debug("setting {} entites", entities.size());
        this.entities.clear();
        this.entities.addAll(entities);
        root.setCount(entities.size());
    }

    @Override public void setUpdateHandler(Runnable updateHandler) {
        if (updateHandler == null) throw new IllegalArgumentException();
        LOGGER.debug("setting update handle");
        this.updateHandler = updateHandler;
    }

    @Override public void refresh() {
        LOGGER.debug("refreshing");
    }

    protected void onUpdate() {
        if (updateHandler != null) {
            updateHandler.run();
        }
    }
}
