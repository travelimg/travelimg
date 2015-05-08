package at.ac.tuwien.qse.sepm.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App extends Application {
    private static final Logger logger = LogManager.getLogger();

    private ClassPathXmlApplicationContext context;

    public static void main(String[] args) {
        launch(args);
    }

    public App() {
        try {
            context = new ClassPathXmlApplicationContext("beans.xml");
        } catch (Exception e) {
            logger.error("Failed to setup application. Exiting.", e);
            System.exit(1);
        }
    }

    @Override public void start(Stage stage) throws Exception {
        logger.info("Application started.");

        MainController mainController = new MainController();
        stage.setScene(new Scene(mainController));
        stage.setTitle("travelimg");
        stage.show();
    }

    @Override public void stop() throws Exception {
        super.stop();

        context.close();
    }
}
