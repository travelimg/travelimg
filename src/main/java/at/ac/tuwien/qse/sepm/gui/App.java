package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.service.impl.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App extends Application
{
    private static final Logger logger = LogManager.getLogger(App.class);
    public static void main( String[] args )
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        //logger
        logger.info("Application started.");

        /*A short demonstration showing how the spring framework instantiates for us
        a new service object without using the new operator. All configs go to beans.xml */
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Service service = (Service) context.getBean("serviceBean");

        //javafx
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/Main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello world!");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }
}
