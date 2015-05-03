package at.ac.tuwien.qse.sepm.gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App extends Application
{
    private static final Logger logger = LogManager.getLogger(App.class);
    public static void main( String[] args )
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        logger.info("Application started.");

        //javafx
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/Main.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();

        mainController.createStructure();
        mainController.setStage(primaryStage);
        primaryStage.setTitle("Hello world!");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }
}
