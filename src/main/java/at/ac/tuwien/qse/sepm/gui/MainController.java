package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

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

    public void initialize(){
        root = new TreeItem<String>("Photos");
        root.setExpanded(true);
        photoTreeView.setRoot(root);
    }

    public void setPhotoService(PhotoService photoService) {
        this.photoService = photoService;
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
    public void onImportPhotosClicked(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose directory pictures"); //maybe we should be able to select specific files from a folder ;)
        //FileChooser.ExtensionFilter allowedFileTypes = new FileChooser.ExtensionFilter("Pictures (*.jpg;*.jpeg;*.png)", "*.jpg","*.jpeg","*.png");
        //chooser.getExtensionFilters().add(allowedFileTypes);
        File selectedDirectory = chooser.showDialog(stage);
        ArrayList<Photo> photos = new ArrayList<Photo>();
        for (final File fileEntry : selectedDirectory.listFiles()) {
            if (!fileEntry.isDirectory() && getExtension(fileEntry.getName()).equals("JPG")) {
                photos.add(new Photo(null, null, fileEntry.getPath(), new Date(fileEntry.lastModified()), 0));
            }
        }
        try {
            importService.importPhotos(photos);
            root.getChildren().clear();
            createStructure();
        } catch (ServiceException e) {
        }

    }

    public String getExtension(String file){
        String extension = "";

        int i = file.lastIndexOf('.');
        if (i > 0) {
            extension = file.substring(i+1);
        }
        return extension;
    }

    public String getMonth(Date d){
        return new SimpleDateFormat("MMM").format(d);
    }

    public String getYear(Date d){
        return new SimpleDateFormat("YYYY").format(d);
    }

    public void setImportService(ImportService importService) {
        this.importService=importService;
    }
}
