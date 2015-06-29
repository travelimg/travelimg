package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.gui.util.ColorUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import org.scribe.builder.api.Foursquare2Api;

public class TitleSlideView extends SlideView {

    private final TitleSlide slide;

    public TitleSlideView(TitleSlide slide, int height, int width) {
        this.slide = slide;

        Background background = new Background(new BackgroundFill(ColorUtils.fromInt(slide.getColor()), null, null));
        Label label = new Label(slide.getCaption());

        setBackground(background);

        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        HBox.setHgrow(label, Priority.ALWAYS);
        HBox box = new HBox();
        box.getChildren().add(label);
        getChildren().add(box);

        label.setTextFill(Paint.valueOf("white"));
        label.setStyle("-fx-text-fill: white !important;");
        label.setStyle("-fx-font-weight: bold");
        label.setStyle("-fx-text-alignment: center");
        label.setStyle("-fx-font-size: 70px");
    }
}
