package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.WikipediaInfoPane;
import at.ac.tuwien.qse.sepm.gui.grid.ImageGrid;
import at.ac.tuwien.qse.sepm.gui.util.GeoUtils;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HighlightsViewController {

    @FXML private BorderPane borderPane, left, FotoContainer,treeBoarder,timeLine;
    @FXML private GoogleMapsScene mapsScene;
    @FXML private VBox journeys,tree, tagheartContainer;
    @FXML private HBox titleHBox,tagContainer,wikipediaInfoPaneContainer, mapContainer, firstFourTagsHBox;
    @FXML private ScrollPane scrollPhotoView, treeScroll;
    @FXML private Label titleLabel;
    @FXML private Button tag1,tag2,tag3,tag4,tag5,good;
    @FXML private StrokeLineCap lineCap;

    @Autowired private ClusterService clusterService;
    @Autowired private PhotoService photoService;
    @Autowired private TagService tagService;
    @Autowired private WikipediaService wikipediaService;

    private ListView<Journey> journeysListView = new ListView<>();
    private HashMap<Place,List<Tag>> placesAndTags = new HashMap<>();
    private HashMap<PlaceDate,List<Photo>> orderedPlacesAndPhotos = new HashMap<>();
    private List<Photo> goodPhotosList = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private List<Button> buttonAr = new LinkedList<>();
    List<Button> tagButtons = new ArrayList<>();
    private Label noJourneysAvailableLabel = new Label("Keine Reisen gefunden. Bitte fügen Sie eine neue ein.");
    private Journey selectedJourney;
    private WikipediaInfoPane wikipediaInfoPane;
    private GoogleMapView mapView;
    private GoogleMap googleMap;
    private Marker actualMarker;
    private Place aktivePlace = null;
    private PhotoFilter filter = new PhotoFilter();
    private Consumer<PhotoFilter> filterChangeCallback;
    private ImageGrid grid;
    private TreeView<String> treeView;
    private ImageCache imageCache;
    private Line redLine;
    private boolean disableReload = false;
    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
        this.grid = new ImageGrid(imageCache);
    }

    public void setMap(GoogleMapsScene map) {
        this.mapsScene = map;
        mapView = map.getMapView();
        mapView.addMapInializedListener(new MapComponentInitializedListener() {
            @Override public void mapInitialized() {
                //wait for the map to initialize.
                googleMap = mapView.getMap();
            }
        });
        mapContainer.getChildren().add(map.getMapView());
    }

    public void initialize(){
        buttonAr.add(tag1);
        buttonAr.add(tag2);
        buttonAr.add(tag3);
        buttonAr.add(tag4);
        tagButtons.addAll(Arrays.asList(tag1,tag2,tag3,tag4,tag5));
        redLine = new Line(100, 50, 100, 500);
        redLine.setStroke(Color.DARKGRAY);
        redLine.setStrokeWidth(2);
        redLine.setStrokeLineCap(StrokeLineCap.ROUND);

        redLine.getStrokeDashArray().addAll(2D, 21D);
        redLine.setStrokeDashOffset(30);

        wikipediaInfoPane = new WikipediaInfoPane(wikipediaService);
        wikipediaInfoPaneContainer.getChildren().add(wikipediaInfoPane);

        journeysListView.setCellFactory(new Callback<ListView<Journey>, ListCell<Journey>>() {

            public ListCell<Journey> call(ListView<Journey> param) {
                final ListCell<Journey> cell = new ListCell<Journey>() {
                    @Override public void updateItem(Journey item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        }
                    }
                }; // ListCell
                return cell;
            }
        }); // setCellFactory

        journeysListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override public void handle(MouseEvent event) {
                handleJourneySelected(journeysListView.getSelectionModel().getSelectedItem());
            }
        });
        noJourneysAvailableLabel.setWrapText(true);
    }

    public void reloadJourneys(){
        //photoView.getChildren().clear();
        tree.getChildren().clear();
        Label lab2 = new Label();
        lab2.setText("Bitte eine Reise auswählen");
        //.getChildren().add(lab2);

        journeys.getChildren().clear();
        journeysListView.getItems().clear();
        try {
            List<Journey> listOfJourneys = clusterService.getAllJourneys();
            if(listOfJourneys.size()>0){
                journeysListView.getItems().addAll(listOfJourneys);
                journeys.getChildren().add(journeysListView);
            }
            else{
                journeys.getChildren().add(noJourneysAvailableLabel);
            }
        } catch (ServiceException e) {
            journeys.getChildren().add(noJourneysAvailableLabel);
        }
    }

    private void handleJourneySelected(Journey journey){

        try {
            /*
                CLEAR THE HASHMAPS
             */
            orderedPlacesAndPhotos.clear();
            placesAndTags.clear();

            HBox placesTitleHBox = new HBox();
            Label placesTitleLabel = new Label("Orte");
            placesTitleLabel.setStyle("-fx-background-color: #333333; -fx-font-size: 18; -fx-text-fill: white; -fx-padding: 5 0 5 10px;");
            placesTitleLabel.setPrefWidth(259);
            placesTitleLabel.setPrefHeight(20);

            Button back = new Button("<");
            back.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event) {
                    clearMap();
                    left.setTop(titleHBox);
                    left.setCenter(journeys);
                }
            });

            placesTitleHBox.getChildren().add(back);
            placesTitleHBox.getChildren().add(placesTitleLabel);
            left.setTop(placesTitleHBox);

            Set<Place> places= clusterService.getPlacesByJourney(journey);


            /*
                merge Places with Photos
                output is a HashSet
             */
            HashMap<Place,List<Photo>> photoToPlace = new HashMap<>();
            for(Place pl : places){
                List<Photo> photos = new ArrayList<>();

                // TODO include PhotoFilter (bug)
                for(Photo p : photoService.getAllPhotos()){
                    if(p.getData().getPlace()!=null) {
                        if (p.getData().getPlace().getId() == pl.getId()) {
                            photos.add(p);
                        }
                    }
                }
                photoToPlace.put(pl,photos);
            }

            // merge Place with DataTime
            HashMap<LocalDateTime,Place> orderedPlaces = new HashMap<>();

            for(Place ple : photoToPlace.keySet()){
                LocalDateTime min = LocalDateTime.MAX;
                for(Photo p: photoToPlace.get(ple)){

                    if(p.getData().getDatetime().compareTo(min)<0){
                        min = p.getData().getDatetime();
                    }
                }
                orderedPlaces.put(min,ple);
            }

            // order the LocalDateTime
            List<LocalDateTime> sortedKeys= new ArrayList<>(orderedPlaces.keySet());
            Collections.reverse(sortedKeys);


            // final HashMap with DateTime, Place and photos
            for(LocalDateTime l : sortedKeys){
                PlaceDate pl = new PlaceDate(orderedPlaces.get(l),l);
                orderedPlacesAndPhotos.put(pl,photoToPlace.get(orderedPlaces.get(l)));
            }


            ArrayList<Place> orderedPlacesList = new ArrayList<>();
            for(PlaceDate pd: orderedPlacesAndPhotos.keySet()){
                orderedPlacesList.add(pd.getPlace());
            }

            VBox v = new VBox();
            v.setSpacing(5.0);
            v.setStyle("-fx-font-size: 16;");
            v.setPadding(new Insets(5.0, 0.0, 0.0, 10.0));
            final ToggleGroup group = new ToggleGroup();
            RadioButton rbAll = new RadioButton("Alle Orte");
            rbAll.setToggleGroup(group);
            rbAll.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent event) {
                    clearMap();
                    drawDestinationsAsPolyline(GeoUtils.toLatLong(new ArrayList<Place>(places)));
                    setGoodPhotos(null);
                    setMostUsedTagsWithPhotos(null);
                }
            });
            v.getChildren().add(rbAll);
            rbAll.setSelected(true);
            clearMap();
            drawDestinationsAsPolyline(GeoUtils.toLatLong(orderedPlacesList));
            setGoodPhotos(null);
            setMostUsedTagsWithPhotos(null);
            reloadImages();

            int pos = 0;
            for(PlaceDate pd: orderedPlacesAndPhotos.keySet()){
                Place p = pd.getPlace();
                RadioButton rb = new RadioButton(p.getCity());
                rb.setToggleGroup(group);
                final int finalPos = pos;
                rb.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent event) {
                        handlePlaceSelected(orderedPlacesList,p, finalPos);
                    }
                });
                v.getChildren().add(rb);
                pos++;
            }
            left.setCenter(v);


        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private void handlePlaceSelected(List<Place> places, Place place, int pos) {
        wikipediaInfoPane.showDefaultWikiInfo(place);
        drawJourneyUntil(places,pos);
        setGoodPhotos(null);
        setMostUsedTagsWithPhotos(null);

        /*aktivePlace = place;
        // set Buttontext default
        for(int i=0; i<buttonAr.size(); i++){
            buttonAr.get(i).setText("default");
        }
        // placesAndTags = die most frequent tags ordered by place
        if(placesAndTags.size()!=0){
            if(placesAndTags.get(place).size()!=0) {

                for (int i = 0; i < placesAndTags.get(place).size(); i++) {
                    // set Button-text to TagName
                    buttonAr.get(i).setText(placesAndTags.get(place).get(i).getName());
                }
            }
        }*/
    }

    public void bt_heartPress(){
        if(goodPhotosList.size()!=0) {
            FullscreenWindow fw = new FullscreenWindow(this.imageCache);
            fw.present(goodPhotosList, goodPhotosList.get(0));
        }
    }

    /**
     * Button event "Tag Buttons"
     */
    public void bt_photosByTag(){
        LOGGER.debug("BUTTON is pressed");
        for(Button b : buttonAr){
            if(b.isArmed() && !b.getText().equals("default")){
                LOGGER.debug("Button:" +b.getText()+"is pressed");
                LOGGER.debug(aktivePlace.toString());
                List<Photo> presentPhotos = new ArrayList<>();

               for(PlaceDate pl :orderedPlacesAndPhotos.keySet()){
                   if(pl.getPlace().getId() == aktivePlace.getId()){
                       LOGGER.debug("same ID");
                       LOGGER.debug("Look @ "+orderedPlacesAndPhotos.get(pl).size()+" Photos");
                       for(Photo p: orderedPlacesAndPhotos.get(pl)){
                           LOGGER.debug(p.toString());
                           for(Tag t: p.getData().getTags()){
                               if(t.getName().equals(b.getText())){
                                   presentPhotos.add(p);
                                   LOGGER.debug("add photo");
                               }
                           }
                       }
                   }
               }
                if(presentPhotos.size()!=0) {
                    FullscreenWindow fw = new FullscreenWindow(this.imageCache);
                    fw.present(presentPhotos, presentPhotos.get(0));
                }else{
                    LOGGER.debug("no Photos");
                }

            }
        }
    }



    public PhotoFilter getFilter(){
        return filter;
    }

    private void handleFilterChange(){
        LOGGER.debug("filter changed");
        if(filterChangeCallback ==null) return;
        filterChangeCallback.accept(getFilter());
    }

    private void handleFilterChange(PhotoFilter filter){
        this.filter = filter;
        tree.getChildren().clear();

        if(!disableReload) reloadImages();
    }

    public void setFilterChangeAction(Consumer<PhotoFilter>callback){
        LOGGER.debug("setting filter change action");
        filterChangeCallback = callback;
    }

    /**
     * Filters all good Photos from the selected journey
     * generate a journeysListView of the most used Tags
     * generate for every Tag(only 5) a TitlePane and fill the pane with the right Fotos
     * add every TitlePane to the GUI
     */
    public void reloadImages(){
        LOGGER.debug("reload Images");

        /*
                NEW FUNCTIONALITY
         */

        for(PlaceDate pl :orderedPlacesAndPhotos.keySet()){
            // mostFreuqentTags to Place
            try {
               List<Tag> list = tagService.getMostFrequentTags(orderedPlacesAndPhotos.get(pl));
                placesAndTags.put(pl.getPlace(),list);
            } catch (ServiceException e) {
                //TODO no tag s found
            }

            // all GOOD fotos
            for(Photo p: orderedPlacesAndPhotos.get(pl)){
                if(p.getData().getRating().equals(Rating.GOOD)){
                    //goodPhotosList.add(p);
                }

            }
        }
        /*

            END

         */





        boolean rbIsSet =false;
        /*for(RadioButton r:journeyRadioButtonsHashMap.keySet()){
            if(r.isSelected()){
                System.out.println(journeyRadioButtonsHashMap.get(r).getName());
                rbIsSet=true;
            }
        }*/
        if(rbIsSet=true) {
            LOGGER.debug("Reise ausgewählt ");
            try {

                List<Photo> allPhotos = photoService.getAllPhotos(filter).stream()
                        .sorted((p1, p2) -> p2.getData().getDatetime().compareTo(p1.getData().getDatetime()))
                        .collect(Collectors.toList());
                LOGGER.debug(photoService.getAllPhotos(filter).size());
                LOGGER.debug(allPhotos.size());

                List<Photo> goodPhotos = new ArrayList<>();
                for (Photo p : allPhotos) {
                    if (p.getData().getRating() == (Rating.GOOD)) {
                        goodPhotos.add(p);
                    }
                }
                LOGGER.debug(goodPhotos.size());
               Collections.sort((ArrayList)goodPhotos);

                if (goodPhotos.size() == 0) {
                    //photoView.getChildren().clear();
                    Label lab = new Label();
                    lab.setText("Es sind kein mit 'good' bewerteten Fotos zu dieser Reise vorhanden");
                    //photoView.getChildren().add(lab);

                } else {
                    try {
                        //get all places with photos from the journey

                        HashMap<Place,List<Photo>> places= new HashMap<>();
                        for(Photo ph:goodPhotos){
                            if(places.size()==0){
                                ArrayList<Photo> list = new ArrayList<>();
                                list.add(ph);

                                places.put(ph.getData().getPlace(),list);
                            }else{
                                if(places.containsKey(ph.getData().getPlace())){
                                    List<Photo> list2 = places.get(ph.getData().getPlace());
                                    list2.add(ph);

                                    places.put(ph.getData().getPlace(),list2);
                                }else{
                                    ArrayList<Photo> list = new ArrayList<>();
                                    list.add(ph);

                                    places.put(ph.getData().getPlace(),list);
                                }
                            }
                        }
                        HashMap<LocalDateTime,Place> orderedPlaces = new HashMap<>();

                        for(Place ple : places.keySet()){

                            LocalDateTime min = LocalDateTime.MAX;
                            for(Photo p: places.get(ple)){
                                if(p.getData().getDatetime().compareTo(min)<0){
                                    min = p.getData().getDatetime();
                                }
                            }
                            orderedPlaces.put(min,ple);
                        }
                        List<LocalDateTime> sortedKeys= new ArrayList<>(orderedPlaces.keySet());
                        Collections.sort(sortedKeys);

                        VBox overall = new VBox();

                        TreeItem<String> rootItem = new TreeItem<>(selectedJourney.getName());
                        for(LocalDateTime time : sortedKeys){
                            Place p = orderedPlaces.get(time);
                            TitledPane tp = new TitledPane();
                            tp.setText(p.getCountry() + " (" + p.getCity() + ") ");

                            //tree
                            TreeItem<String> ti = new TreeItem<>(p.getCountry() + " (" + p.getCity() + ") ");

                            VBox tagTitle = new VBox();

                            List<Tag> taglist = tagService.getMostFrequentTags(places.get(p));
                            for (Tag t : taglist) {
                                List<Photo> name = new ArrayList<>();
                                int counter = 0;
                                for (Photo ph : places.get(p)) {
                                    for (Tag t2 : ph.getData().getTags()) {
                                        if (t.getId() == t2.getId() && counter < 5) {
                                            name.add(ph);
                                            counter++;
                                        }
                                    }
                                }

                                ImageGrid grid2 = new ImageGrid(imageCache);
                                grid2.setPhotos(name);
                                TitledPane tp2 = new TitledPane(t.getName(), grid2);
                                //Tree
                                ti.getChildren().add(new TreeItem<String>(t.getName()));

                                tagTitle.getChildren().add(tp2);
                            }
                            tp.setContent(tagTitle);
                            rootItem.getChildren().add(ti);
                            overall.getChildren().add(tp);
                        }
                        treeView = new TreeView<>(rootItem);

                        String style =" -fx-font-size: 14; -fx-text-fill: #333333; -fx-padding: 5 0 5 10px;";


                        for(Place p : places.keySet()){
                            LocalDateTime pTime = places.get(p).get(0).getData().getDatetime();
                            String text = pTime.getYear()+"-"+pTime.getMonthValue()+"-"+pTime.getDayOfMonth()+"    -" +p.getCountry();

                            Label lab = new Label();
                            lab.setFont(new Font(18));
                            lab.setStyle(style);
                            lab.setText("-| " + text);

                            tree.getChildren().add(lab);

                            tree.getChildren().add(new Label());

                        }


                        //photoView.getChildren().addAll(overall);

                    } catch (ServiceException e) {
                        LOGGER.debug("Photos habe keine Tag's ", e);
                        //photoView.getChildren().clear();
                        grid = new ImageGrid(imageCache);
                        grid.setPhotos(goodPhotos);
                        //photoView.getChildren().add(grid);
                    }
                }
            } catch (ServiceException e) {
                //photoView.getChildren().clear();
                Label lab = new Label();
                lab.setText("Keine Fotos vorhanden");
                //photoView.getChildren().add(lab);
            }
        }else{
            //photoView.getChildren().clear();
            Label lab = new Label();
            lab.setText("Bitte eine Reise auswählen");
            //photoView.getChildren().add(lab);
        }
    }

    /**
     * Sets the good rated photos for the heart button based on a filter.
     * @param filter
     */
    private void setGoodPhotos(PhotoFilter filter){
        try {
            goodPhotosList = photoService.getAllPhotos(); //TODO will use here the filter to get the GOOD rated photos.
            setBackroundImageForButton(goodPhotosList.get(0).getPath(),good);
            if(goodPhotosList.isEmpty()){
                good.setText("No photos");
            }
        } catch (ServiceException e) {
            good.setText("No photos");
        }
    }

    private void setMostUsedTagsWithPhotos(PhotoFilter photoFilter){
        try {
            List<Photo> list = photoService.getAllPhotos(); //TODO will use here the filter
            List<Tag> mostUsedTags = tagService.getMostFrequentTags(list);
            for(int i = 0; i<mostUsedTags.size(); i++){
                Tag t = mostUsedTags.get(i);
                Button tagButton = tagButtons.get(i);
                tagButton.setText(t.getName());
                setBackroundImageForButton(list.get(0).getPath(),tagButton);
                //TODO
                /* get the tagged photos */
                tagButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent event) {
                        if(goodPhotosList.size()!=0) {
                            FullscreenWindow fw = new FullscreenWindow(imageCache);
                            fw.present(list, list.get(0));
                        }
                    }
                });

            }
            int remainingEmptyTagButtons = 5 - mostUsedTags.size();
            fillWithPhotos(remainingEmptyTagButtons,photoFilter);

        } catch (ServiceException e) {
            fillWithPhotos(5,photoFilter);
        }

    }

    private void fillWithPhotos(int nrOfPhotos, PhotoFilter photoFilter) {

        try {
            List<Photo> list = photoService.getAllPhotos(); //TODO will use here the filter
            int i = 0;
            while (nrOfPhotos > 0 && i < list.size()) {
                Button tagButton = tagButtons.get(5-nrOfPhotos);
                tagButton.setText("");
                setBackroundImageForButton(list.get(i).getPath(),tagButton);
                nrOfPhotos--;
                i++;
            }
        } catch (ServiceException e) {

        }
    }

    /**
     * Sets an image as the background of the button
     * @param path the path to the image
     * @param button
     */
    private void setBackroundImageForButton(String path, Button button){
        try {
            button.setStyle("-fx-background-image: url('"+Paths.get(path).toUri().toURL().toString()+"');"+
                    "-fx-background-size: 100% 100%;");
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to convert photo path to URL", e);
        }
    }

    private void drawDestinationsAsPolyline(LatLong[] path){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.path(new MVCArray(path))
                .clickable(false)
                .draggable(false)
                .editable(false)
                .strokeColor("#ff4500")
                .strokeWeight(2)
                .visible(true);
        Polyline polyline = new Polyline(polylineOptions);
        googleMap.addMapShape(polyline);
        polylines.add(polyline);
        GeoUtils.fitMarkersToScreen(path, 0, path.length - 1, mapContainer.getHeight(), mapContainer.getWidth(), mapsScene);
        for(int i = 0; i<path.length;i++) {
            Marker m = new Marker(new MarkerOptions().position(path[i]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
            googleMap.addMarker(m);
            markers.add(m);
        }
    }

    private void drawJourneyUntil(List<Place> places, int pos) {
        clearMap();
        LatLong [] path = GeoUtils.toLatLong(places);
        if(pos==0){
            Marker m = new Marker(new MarkerOptions().position(path[pos]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
            googleMap.addMarker(m);
            markers.add(m);
            GeoUtils.fitMarkersToScreen(path, pos, pos, mapContainer.getHeight(), mapContainer.getWidth(), mapsScene);
        }
        else{
            PolylineOptions polylineOptions = new PolylineOptions();
            MVCArray mvcArray = new MVCArray();
            for(int i = 0; i<=pos; i++){
                mvcArray.push(path[i]);
                Marker m = new Marker(new MarkerOptions().position(path[i]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
                googleMap.addMarker(m);
                markers.add(m);
            }
            polylineOptions.path(mvcArray)
                    .clickable(false)
                    .draggable(false)
                    .editable(false)
                    .strokeColor("#ff4500")
                    .strokeWeight(2)
                    .visible(true);
            Polyline polyline = new Polyline(polylineOptions);
            googleMap.addMapShape(polyline);
            polylines.add(polyline);
            GeoUtils.fitMarkersToScreen(path, pos - 1, pos, mapContainer.getHeight(),
                    mapContainer.getWidth(), mapsScene);
        }
        actualMarker = new Marker(new MarkerOptions().position(path[pos]));
        googleMap.addMarker(actualMarker);
    }

    private void clearMap(){
        for(Marker m : markers){
            googleMap.removeMarker(m);
        }
        markers.clear();
        for(Polyline p : polylines){
            googleMap.removeMapShape(p);
        }
        polylines.clear();
        if(actualMarker!=null){
            googleMap.removeMarker(actualMarker);
        }
    }

    /**
     *  private Class connecting Place with LocalDateTime
     */
    private class PlaceDate {
        private Place place;
        private LocalDateTime date;

        public PlaceDate(Place p, LocalDateTime l){
            this.place=p;
            this.date=l;
        }
        public void setPlace(Place p){
            this.place=p;
        }
        public void setDate(LocalDateTime l){
            this.date=l;
        }
        public Place getPlace(){
            return this.place;
        }
        public LocalDateTime getDate(){
            return this.date;
        }
    }
}
