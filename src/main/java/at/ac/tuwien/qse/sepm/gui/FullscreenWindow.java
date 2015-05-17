package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FullscreenWindow extends Pane {

    private static final Logger logger = LogManager.getLogger();

    private Stage stage;
    private Scene scene;
    private Photo photo;
    private Image image;

    int slideshowcount;

    @FXML
    private ImageView current;

    @FXML
    private ImageView imageView;

    @FXML
    private ImageView next;

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

    public void present(Photo photo,ObservableList<Node> c)
    {
        for(int i = 0; i <c.size(); i++)
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        image = new Image(new FileInputStream(new File(photo.getPath())), 0, 0, true, true);
                    } catch (FileNotFoundException ex) {
                        logger.error("Could not find photo", ex);
                        return;
                    }

                    imageView.setImage(image);
                    slideshowcount++;
                    if(slideshowcount >= c.size())
                    {
                        slideshowcount=0;
                    }
                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        this.photo = photo;
        logger.info(c.size());
        //image = new Image(new File(photo.getPath()));

        /*try {
            image = new Image(new FileInputStream(new File(photo.getPath())), 0, 0, true, true);
        } catch (FileNotFoundException ex) {
            logger.error("Could not find photo", ex);
            return;
        }*/


        //current.setImage(image);
        //photo.setPath("blabla");
        //logger.info(photo.getPath());

        /*

        logger.info("before Image Load");
        Image image = null;
        try {
            image = new Image(new FileInputStream(new File(photo.getPath())), 0, 0, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        logger.info("after image load"+image);

        if(image!=null)
            startImage(image,c);

        logger.info(image);
        */

        stage.show();


    }

    public void startImage(Image image, ObservableList<Node> c)
    {

        if(current !=null)
            c.remove(current);

        current = next;
        next = null;

        next = new ImageView(image);

        next.setFitHeight(720);
        next.setFitHeight(580);
        next.setPreserveRatio(true);
        next.setOpacity(0);

        c.add(next);

        FadeTransition fadein = new FadeTransition(Duration.seconds(1),next);

        fadein.setFromValue(0);
        fadein.setToValue(1);

        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        SequentialTransition st;

        if(current != null) {
            ScaleTransition dropout;

            dropout = new ScaleTransition(Duration.seconds(1), current);
            dropout.setInterpolator(Interpolator.EASE_OUT);
            dropout.setFromX(1);
            dropout.setFromY(1);
            dropout.setToX(0.75);
            dropout.setToY(0.75);
            st = new SequentialTransition(new ParallelTransition(fadein, dropout), delay);
        }
        else
        {
            st = new SequentialTransition(fadein,delay);


        }

        st.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
                Image image = getNextImage();

                if(image !=null)
                    startImage(image,c);

            }
        });

        st.playFromStart();


    }

    BlockingQueue<Image> images = new ArrayBlockingQueue(5);

    Image getNextImage()
    {
        images.poll();

        try {
            return images.take();
        }
        catch(InterruptedException ex)
        {
            logger.error(ex.toString());
        }
        return null;
    }


    public FileVisitResult visitFile(Path t)
    {
        try {
            Image image = new Image(photo.getPath(), 0, 0, true, true);


            if (!image.isError()) {
                images.put(image);
            }
        }
        catch(InterruptedException ex)
        {
            logger.error("Fehler!!!");
            return FileVisitResult.TERMINATE;
        }

        return FileVisitResult.CONTINUE;



    }








}
