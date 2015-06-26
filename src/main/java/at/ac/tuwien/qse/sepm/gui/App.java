package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.service.PhotoService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App extends Application {
    private static final Logger logger = LogManager.getLogger();

    private ClassPathXmlApplicationContext context;

    public App() {
        context = new ClassPathXmlApplicationContext("beans.xml");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Application started.");

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);

        // set base location so that resources can be loaded using relative paths
        loader.setLocation(getClass().getClassLoader().getResource("view"));

        Parent root = loader.load(getClass().getClassLoader().getResourceAsStream("view/Main.fxml"));

        stage.setScene(new Scene(root));

        stage.setTitle("travelimg");
        stage.getIcons().add(getApplicationIcon());
        stage.show();

        PhotoService photoService = (PhotoService)context.getBean("photoService");
        photoService.synchronize();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        context.close();
    }

    private Image getApplicationIcon() {
        return new Image(App.class.getClassLoader().getResourceAsStream("graphics/tmg_logo.png"));
    }
}
