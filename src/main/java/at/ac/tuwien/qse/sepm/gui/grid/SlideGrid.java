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

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.dialogs.ResultDialog;
import at.ac.tuwien.qse.sepm.gui.slide.SlideCallback;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class SlideGrid extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final List<SlideGridNode> nodes = new LinkedList<>();
    private Slideshow slideshow = null;

    private SlideCallback<Void> slideSelectedCallback = null;
    private SlideCallback<Integer> slideAddedCallback = null;
    private SlideCallback<Void> slideChangedCallback = null;

    public SlideGrid() {
        getStyleClass().add("slide-grid");
        setAlignment(Pos.TOP_CENTER);
    }

    public void addSlide(PhotoSlide slide) {
        PhotoSlideTile tile = new PhotoSlideTile(slide);
        addTile(tile);
    }

    public void addSlide(MapSlide slide) {
        MapSlideTile tile = new MapSlideTile(slide);
        addTile(tile);
    }

    public void addSlide(TitleSlide slide) {
        TitleSlideTile tile = new TitleSlideTile(slide);
        addTile(tile);
    }

    private void addTile(SlideTile tile) {
        tile.setOnMouseClicked(event -> handleTileClicked(tile, event));
        SlideGridNode node = new SlideGridNode(tile);
        node.setSlidePositionChangeCallback(this::handleSlidePositionChange);
        node.setSlideAddedCallback(this::handleSlideAdded);

        nodes.add(node);
    }

    public void setSlideshow(Slideshow slideshow) {
        this.slideshow = slideshow;

        nodes.clear();
        getChildren().clear();

        if (slideshow == null) {
            return;
        }

        slideshow.getPhotoSlides().forEach(this::addSlide);
        slideshow.getMapSlides().forEach(this::addSlide);
        slideshow.getTitleSlides().forEach(this::addSlide);

        Collections.sort(nodes, (n1, n2) -> n1.getTile().getSlide().getOrder().compareTo(n2.getTile().getSlide().getOrder()));
        nodes.stream().forEach((node) -> getChildren().add(node));
    }

    public void deselectAll() {
        LOGGER.debug("deselecting all items");
        nodes.forEach(n -> n.getTile().deselect());
    }

    public void setSlideSelectedCallback(SlideCallback<Void> callback) {
        this.slideSelectedCallback = callback;
    }

    public void setSlideAddedCallback(SlideCallback<Integer> callback) {
        this.slideAddedCallback = callback;
    }

    public void setSlideChangedCallback(SlideCallback<Void> callback) {
        this.slideChangedCallback = callback;
    }

    private void handleSlidePositionChange(SlideTile target, Integer sourceId) {
        LOGGER.debug("{} dropped on {}", sourceId, target.getSlide().getId());

        Optional<SlideGridNode> targetNode = nodes.stream()
                .filter(n -> n.getTile().getSlide().getId().equals(target.getSlide().getId()))
                .findFirst();
        Optional<SlideGridNode> sourceNode = nodes.stream()
                .filter(n -> n.getTile().getSlide().getId().equals(sourceId))
                .findFirst();

        if (!targetNode.isPresent() || !sourceNode.isPresent()) {
            return;
        }

        // change the order of the nodes to reflect the drop operation by
        // constructing a new list and moving the source node just before the target node
        List<SlideGridNode> nodesNewOrder = new ArrayList<>();
        for (SlideGridNode node : nodes) {
            if (node == sourceNode.get()) {
                continue; // skip source node, inserted before target node
            }

            if (node == targetNode.get()) {
                // insert source node before target node
                nodesNewOrder.add(sourceNode.get());
                nodesNewOrder.add(targetNode.get());
            } else {
                nodesNewOrder.add(node);
            }
        }

        // set the order attribute of the slides
        int order = 1;
        for (SlideGridNode node : nodesNewOrder) {
            node.getTile().getSlide().setOrder(order);
            order++;

            node.getTile().update();
        }

        nodes.clear();
        nodes.addAll(nodesNewOrder);

        getChildren().clear();
        getChildren().addAll(nodes);

        // notify changes
        if (slideChangedCallback != null) {
            for (SlideGridNode node : nodes) {
                Slide slide = node.getTile().getSlide();

                if (slide instanceof PhotoSlide) {
                    slideChangedCallback.handle((PhotoSlide) slide);
                } else if (slide instanceof MapSlide) {
                    slideChangedCallback.handle((MapSlide) slide);
                } else if (slide instanceof TitleSlide) {
                    slideChangedCallback.handle((TitleSlide) slide);
                }
            }
        }
    }

    private void handleSlideAdded(SlideGridNode successor) {
        LOGGER.debug("Added slide before {}", successor.getTile().getSlide().getId());

        int index = successor.getTile().getSlide().getOrder() - 1;

        SlideTypeDialog dialog = new SlideTypeDialog(this);
        Optional<SlideType> type = dialog.showForResult();

        if (type.isPresent()) {
            if (type.get() == SlideType.MAP) {
                MapSlide slide = new MapSlide(-1, 0, 0, "", 0, 0, 10);
                slideAddedCallback.handle(slide, index);
            } else {
                TitleSlide slide = new TitleSlide(-1, 0, 0, "", 0);
                slideAddedCallback.handle(slide, index);
            }
        }
    }

    private void handleTileClicked(SlideTile tile, MouseEvent event) {
        deselectAll();
        tile.select();

        if (slideSelectedCallback != null) {
            Slide slide = tile.getSlide();

            if (slide instanceof PhotoSlide) {
                slideSelectedCallback.handle((PhotoSlide) slide);
            } else if (slide instanceof MapSlide) {
                slideSelectedCallback.handle((MapSlide) slide);
            } else if (slide instanceof TitleSlide) {
                slideSelectedCallback.handle((TitleSlide) slide);
            }
        }
    }

    private enum SlideType {
        MAP,
        TITLE
    }

    private static class SlideTypeDialog extends ResultDialog<SlideType> {
        @FXML
        private Button mapButton;
        @FXML
        private Button titleButton;

        public SlideTypeDialog(Node origin) {
            super(origin, "Bitte Folientyp wählen");
            FXMLLoadHelper.load(this, this, SlideTypeDialog.class, "view/SlideTypeDialog.fxml");

            mapButton.setOnAction((event) -> {
                setResult(SlideType.MAP);
                close();
            });

            titleButton.setOnAction((event) -> {
                setResult(SlideType.TITLE);
                close();
            });
        }
    }
}
