package at.ac.tuwien.qse.sepm.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class App extends Application {
    private static final Logger logger = LogManager.getLogger();

    private ClassPathXmlApplicationContext context;

    public App() {
        try {
            context = new ClassPathXmlApplicationContext("beans.xml");
        } catch (Exception e) {
            logger.error("Failed to setup application. Exitingig.", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Application started.");

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return context.getBean(param);
            }
        });

        // set base location so that resources can be loaded using relative paths
        loader.setLocation(getClass().getClassLoader().getResource("view"));

        Parent root;
        try {
            root = loader.load(getClass().getClassLoader().getResourceAsStream("view/Main.fxml"));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.setTitle("travelimg");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        context.close();
    }
}
