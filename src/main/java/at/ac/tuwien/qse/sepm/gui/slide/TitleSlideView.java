package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;

public class TitleSlideView extends SlideView {

    private final TitleSlide slide;

    public TitleSlideView(TitleSlide slide) {
        this.slide = slide;

        Background background = new Background(new BackgroundFill(Paint.valueOf("black"), null, null));
        Label label = new Label(slide.getCaption());

        setBackground(background);
        getChildren().add(label);

        setAlignment(label, Pos.CENTER);
    }
}
