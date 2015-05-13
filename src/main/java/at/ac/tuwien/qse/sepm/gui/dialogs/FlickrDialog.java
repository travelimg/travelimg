package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.service.FlickrService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.FlickrServiceImpl;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

public class FlickrDialog extends Dialog {

    @FXML private HBox progress;
    @FXML private ProgressBar progressBar;
    @FXML private TilePane tilePane;
    @FXML private Button button;

    @Autowired private FlickrService flickrService;

    public FlickrDialog(Node origin, String title) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, FlickrDialog.class, "view/FlickrDialog.fxml");
        progress.setVisible(true);
        flickrService = new FlickrServiceImpl();
        loadPhotos();

    }

    @FXML
    public void handleOnLoadMoreButtonClicked(){
        //button.setDisable(true);
        loadPhotos();
    }

    private void loadPhotos(){

        String tags [] = {"mailand"};
        try {
            flickrService.downloadPhotos(null,41.891235, 12.491597,false,true,new Consumer<Photo>() {
                double d = 0.0;
                public void accept(Photo photo) {

                    Platform.runLater(new Runnable() {

                        public void run() {
                            ImageView imageView = null;
                            try {
                                final Image image;
                                image = new Image(new FileInputStream(new File(photo.getPath())), 150, 0, true, true);
                                imageView = new ImageView(image);
                                imageView.setFitWidth(150);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            tilePane.getChildren().add(imageView);
                            d++;
                            progressBar.setProgress(d / 10.0);
                        }
                    });

                }
            }, new ErrorHandler<ServiceException>() {

                public void handle(ServiceException exception) {
                    //handle errors here
                }
            });
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
