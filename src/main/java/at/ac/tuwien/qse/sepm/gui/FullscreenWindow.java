package at.ac.tuwien.qse.sepm.gui;


import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FullscreenWindow extends Pane {

    private Stage stage;
    private Scene scene;

    public FullscreenWindow() {
        FXMLLoadHelper.load(this, this, FullscreenWindow.class, "view/FullScreenDialog.fxml");



    }

    @FXML
    private void initialize() {
        this.stage = new Stage();
        this.scene = new Scene(this);

        stage.setScene(scene);
        stage.setFullScreen(true);

    }

    public void present() {
        stage.showAndWait();
    }

}
