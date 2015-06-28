package at.ac.tuwien.qse.sepm.gui.control;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * A simple keyword with a close button.
 */
public class Keyword extends HBox {

    private String name;
    private final Text x = new Text("x");

    public Keyword(String name){
        super();
        this.name = name;
        setStyle("-fx-background-radius: 5; -fx-background-color: -tmg-primary; ");
        setAlignment(Pos.CENTER);
        setPadding(new Insets(3, 5, 5, 5));
        Text text = new Text(name+"  ");
        text.setFill(Color.WHITE);
        getChildren().add(text);
        getChildren().add(x);
        x.setOnMouseEntered(event -> setCursor(Cursor.HAND));
        x.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
    }

    public String getName() {
        return name;
    }

    public void setOnClosed(EventHandler eventHandler){
        x.setOnMouseClicked(eventHandler);
    }

}
