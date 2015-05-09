package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.ImportServiceImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.applet.Main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class MainController {

    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TreeView photoTreeView;

    private TreeItem<String> root;
    private Stage stage;
    private PhotoService photoService;
    private ImportService importService;

    public MainController() {
    }

    public void setContext(ApplicationContext context) {
        photoService = (PhotoService) context.getBean("photoService");
        importService = (ImportService) context.getBean("importService");
    }

    public void initialize(){
        root = new TreeItem<String>("Photos");
        root.setExpanded(true);
        photoTreeView.setRoot(root);
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void createStructure(){
        String year = "";
        String month ="";
        TreeItem<String> y = null;
        TreeItem<String> m = null;
        try {
            List<Photo> photos = photoService.getAllPhotos();
            for(Photo p : photos){
                Date date = new Date(/*p.getExif().getDate().getTime()*/);
                if(!getYear(date).equals(year)){
                    y = new TreeItem<String>(getYear(date));
                    m = new TreeItem<String>(getMonth(date));
                    m.getChildren().add(new TreeItem<String>(p.getPath()));
                    y.getChildren().add(m);
                    root.getChildren().add(y);
                    year = getYear(date);
                    month = getMonth(date);
                }
                else{
                    if(!getMonth(date).equals(month)){
                        m = new TreeItem<String>(getMonth(date));
                        m.getChildren().add(new TreeItem<String>(p.getPath()));
                        y.getChildren().add(m);
                        month = getMonth(date);
                    }
                    else{
                        m.getChildren().add(new TreeItem<String>(p.getPath()));
                    }
                }
            }
        } catch (ServiceException e) {
            //TODO some dialog maybe? ;)
        }
    }

    @FXML
    public void onImportPhotosClicked()  {
        ImportDialog dialog = new ImportDialog(stage);

        Optional<List<Photo>> optionalPhotos = dialog.run();

        if(!optionalPhotos.isPresent())
            return;

        List<Photo> photos = optionalPhotos.get();

        Consumer<Photo> callback = new Consumer<Photo>() {
            @Override
            public void accept(Photo photo) {
                System.out.println("Imported photo");
                // TODO: Add photo to image grid
            }
        };

        ServiceExceptionHandler errorHandler = new ServiceExceptionHandler() {
            @Override
            public void handle(ServiceException exception) {
                logger.error("Import failed", exception);
                // TODO: notify user about error
            }
        };

        importService.importPhotos(photos, callback, errorHandler);
    }

    public String getMonth(Date d){
        return new SimpleDateFormat("MMM").format(d);
    }

    public String getYear(Date d){
        return new SimpleDateFormat("YYYY").format(d);
    }
}
