package at.ac.tuwien.qse.sepm.gui.control;

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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * A special popup control showing progress using a progressbar.
 */
public class DownloadProgressControl extends PopupControl {

    private final BorderPane borderPane = new BorderPane();
    private final ProgressBar progressBar = new ProgressBar();
    private Button button;
    private FadeTransition ft;
    private String buttonStyle;
    private Tooltip buttonTooltip;
    private EventHandler buttonOnAction;

    public DownloadProgressControl(Button button){
        super();
        this.button = button;
        this.buttonStyle = button.getGraphic().getStyle();
        this.buttonTooltip = button.getTooltip();
        this.buttonOnAction = button.getOnAction();
        this.ft = new FadeTransition(Duration.millis(1000), button);

        ft.setFromValue(1.0);
        ft.setToValue(0.3);
        ft.setCycleCount(Animation.INDEFINITE);
        ft.setAutoReverse(false);
        ft.play();

        Label label = new Label("Fotos werden heruntergeladen");
        label.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(5.0,5.0,5.0,5.0));
        hBox.getChildren().add(progressBar);
        progressBar.setProgress(0.0);

        borderPane.setStyle("-fx-background-color: #f5f5b5; -fx-background-radius: 5.0; -fx-border-radius: 5.0; -fx-border-color: black; -fx-border-width: 0.5;");
        borderPane.setPadding(new Insets(2.0, 2.0, 2.0, 2.0));
        borderPane.setCenter(label);
        borderPane.setBottom(hBox);
        getScene().setRoot(borderPane);

        button.setOnMouseEntered(event -> handleShow());
        button.setOnMouseExited(event -> fadeOut());
    }

    public void setProgress(double progress){
        progressBar.setProgress(progress);
    }

    public void finish(boolean interrupted){
        ft.stop();
        borderPane.getChildren().clear();

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5.0,5.0,5.0,5.0));
        hBox.setAlignment(Pos.CENTER);
        if(!interrupted){
            hBox.getChildren().add(new Label("Fotos heruntergeladen "));
            hBox.getChildren().add(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
        }
        else{
            hBox.getChildren().add(new Label("Herunterladen fehlgeschlagen "));
            hBox.getChildren().add(new FontAwesomeIconView(FontAwesomeIcon.TIMES));
        }

        borderPane.setCenter(hBox);

        button.setOnMouseExited(e -> {
            fadeOut();
            button.setOnMouseEntered(null);
            button.setOnMouseExited(null);
            button.setTooltip(buttonTooltip);
            button.getGraphic().setStyle(buttonStyle);
        });

        button.setOnAction(buttonOnAction);
    }

    private void handleShow(){
        Point2D p = button.localToScene(button.getLayoutBounds().getMinX(), button.getLayoutBounds().getMinY());
        show(button,p.getX() + button.getScene().getX() + button.getScene().getWindow().getX()+55.0,p.getY() + button.getScene().getY() + button.getScene().getWindow().getY()-35.0);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), borderPane);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void fadeOut(){
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), borderPane);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }

}
