package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.applet.Main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainController {

    @FXML
    private TreeView photoTreeView;

    private TreeItem<String> root;
    private Stage stage;
    private PhotoService photoService;
    private ImportService importService;

    public MainController() {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

        photoService = (PhotoService) context.getBean("photoServiceBean");
        importService = (ImportService) context.getBean("importServiceBean");

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
                if(!getYear(p.getDate()).equals(year)){
                    y = new TreeItem<String>(getYear(p.getDate()));
                    m = new TreeItem<String>(getMonth(p.getDate()));
                    m.getChildren().add(new TreeItem<String>(p.getPath()));
                    y.getChildren().add(m);
                    root.getChildren().add(y);
                    year = getYear(p.getDate());
                    month = getMonth(p.getDate());
                }
                else{
                    if(!getMonth(p.getDate()).equals(month)){
                        m = new TreeItem<String>(getMonth(p.getDate()));
                        m.getChildren().add(new TreeItem<String>(p.getPath()));
                        y.getChildren().add(m);
                        month = getMonth(p.getDate());
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
    public void onImportPhotosClicked() {
        ImportDialog dialog = new ImportDialog(importService, stage);
        dialog.showAndWait();
    }

    public String getMonth(Date d){
        return new SimpleDateFormat("MMM").format(d);
    }

    public String getYear(Date d){
        return new SimpleDateFormat("YYYY").format(d);
    }
}
