package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.gui.util.ColorUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class TitleSlideTile extends SlideTileBase<TitleSlide> {

    private final Pane background = new Pane();
    private final FontAwesomeIconView icon = new FontAwesomeIconView();

    public TitleSlideTile(TitleSlide slide) {
        super(slide);
        getStyleClass().add("title");

        getChildren().add(0, icon);

        Color color = ColorUtils.fromInt(slide.getColor());
        System.out.println("Color: " + color);
        background.setStyle(String.format("-fx-background-color: rgb(%d, %d, %d)",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255)));
        getChildren().add(0, background);
    }
}
