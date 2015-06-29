package at.ac.tuwien.qse.sepm.gui.slide;

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
