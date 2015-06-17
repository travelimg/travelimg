package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.FilterList;
import at.ac.tuwien.qse.sepm.gui.grid.ImageGrid;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.TagService;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.sun.javafx.scene.text.TextLine;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Collections.*;

public class HighlightsViewController {

    @FXML private BorderPane borderPane,FotoContainer,treeBoarder,timeLine;
    @FXML private GoogleMapsScene mapsScene;
    @FXML private VBox journeys, mapContainer, photoView,tree;
    @FXML private FilterList<Journey> journeyListView;
    @FXML private ScrollPane scrollPhotoView, treeScroll;

    @Autowired private ClusterService clusterService;
    @Autowired private PhotoService photoService;
    @Autowired private TagService tagService;
    private PhotoFilter filter = new PhotoFilter();
    private Journey selectedJourney;
    private GoogleMapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private ArrayList<Polyline> polylines = new ArrayList<Polyline>();
    private List<Place> places;
    private HashMap<RadioButton,Journey> journeyRadioButtonsHashMap = new HashMap<>();
    private Marker actualMarker;
    private boolean disableReload = false;
    private Consumer<PhotoFilter> filterChangeCallback;
    private ImageGrid grid;
    private int pos = 0;
    private TreeView<String> treeView;
    private static final Logger LOGGER = LogManager.getLogger();
    private ImageCache imageCache;
    private Line redLine;

    @FXML
    private StrokeLineCap lineCap;

    @Autowired
    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
        this.grid = new ImageGrid(imageCache);
    }

    public void initialize(){
        /**to remove - BEGIN**/
        HBox vBox = new HBox();
        Button playButton = new Button("Play selected journey!");
        playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override public void handle(MouseEvent event) {
                if (pos == 0) {
                    clearMap();
                }
                playTheJourney(pos);
                pos++;
            }
        });
        vBox.getChildren().add(playButton);
        borderPane.setBottom(vBox);
        /**to remove - END**/
        redLine = new Line(100, 50, 100, 500);
        redLine.setStroke(Color.DARKGRAY);
        redLine.setStrokeWidth(2);
        redLine.setStrokeLineCap(StrokeLineCap.ROUND);

        redLine.getStrokeDashArray().addAll(2D, 21D);
        redLine.setStrokeDashOffset(30);




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

    public void reloadJourneys(){
        journeyRadioButtonsHashMap.clear();
        photoView.getChildren().clear();
        tree.getChildren().clear();

        Label lab2 = new Label();
        lab2.setText("Bitte eine Reise auswählen");
        photoView.getChildren().add(lab2);

        try {
            List<Journey> listOfJourneys = clusterService.getAllJourneys();
            if(listOfJourneys.size()>0){
                journeys.getChildren().clear();
            }
            final ToggleGroup group = new ToggleGroup();
            for(Journey j: listOfJourneys){
                RadioButton rb = new RadioButton(j.getName());
                rb.setOnAction(this::handleSelectionChange);
                rb.setToggleGroup(group);
                journeys.getChildren().add(rb);
                journeyRadioButtonsHashMap.put(rb,j);
            }
        } catch (ServiceException e) {
            Label lab = new Label();
            lab.setText("keine Reisen vorhanden");
            journeys.getChildren().add(lab);
        }
    }

    private void handleSelectionChange(ActionEvent e) {
        clearMap();
        Journey j = journeyRadioButtonsHashMap.get((RadioButton) e.getSource());
        selectedJourney = j;
        LOGGER.debug("Selected journey {}",j.getName());
        try {
            places = clusterService.getPlacesByJourney(j);
            if(places.size()>0){
                drawDestinationsAsPolyline(toLatLong(places));
            }

        } catch (ServiceException e1) {

        }
        filter.getIncludedJourneys().clear();
        filter.getIncludedJourneys().add(j);
        handleFilterChange(filter);
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
     * generate a list of the most used Tags
     * generate for every Tag(only 5) a TitlePane and fill the pane with the right Fotos
     * add every TitlePane to the GUI
     */
    public void reloadImages(){
        boolean rbIsSet =false;
        for(RadioButton r:journeyRadioButtonsHashMap.keySet()){
            if(r.isSelected()){
                System.out.println(journeyRadioButtonsHashMap.get(r).getName());
                rbIsSet=true;
            }
        }
        if(rbIsSet=true) {
            try {

                List<Photo> allPhotos = photoService.getAllPhotos(filter).stream()
                        .sorted((p1, p2) -> p2.getData().getDatetime().compareTo(p1.getData().getDatetime()))
                        .collect(Collectors.toList());
                List<Photo> goodPhotos = new ArrayList<>();
                for (Photo p : allPhotos) {
                    if (p.getData().getRating() == (Rating.GOOD)) {
                        goodPhotos.add(p);
                    }
                }
               Collections.sort((ArrayList)goodPhotos);

                if (goodPhotos.size() == 0) {
                    photoView.getChildren().clear();
                    Label lab = new Label();
                    lab.setText("Es sind kein mit 'good' bewerteten Fotos zu dieser Reise vorhanden");
                    photoView.getChildren().add(lab);

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

                       /* tree.getChildren().add(redLine);

                        double distance = redLine.getEndY()-redLine.getStartY();

                        int anz = places.size();
                        double dist = (500 / anz)/2;
                        int counter =1;
                        Text start = new Text(82,50,"START");
                        start.setStroke(javafx.scene.paint.Paint.valueOf("DARKGRAY"));
                        tree.getChildren().add(start);
                        Text end = new Text(88,545,"END");
                        end.setStroke(javafx.scene.paint.Paint.valueOf("DARKGRAY"));
                        tree.getChildren().add(end);*/

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
                           /* Line l = new Line(91,50 + counter * dist,109,50 + counter * dist);
                            l.setStroke(Color.DARKGRAY);
                            l.setStrokeWidth(4);
                            l.setStrokeLineCap(StrokeLineCap.ROUND);
                            Text text = new Text(120,53 + counter * dist,p.getCountry());
                            LocalDateTime pTime = places.get(p).get(0).getDatetime();
                            Text text2 = new Text(4,53 + counter * dist,pTime.getYear()+"-"+pTime.getMonthValue()+"-"+pTime.getDayOfMonth());
                            tree.getChildren().addAll(l,text,text2);
                            counter++;*/
                        }

                       // tree.getChildren().add(treeView);

                        photoView.getChildren().addAll(overall);

                    } catch (ServiceException e) {
                        LOGGER.debug("Photos habe keine Tag's ", e);
                        photoView.getChildren().clear();
                        grid = new ImageGrid(imageCache);
                        grid.setPhotos(goodPhotos);
                        photoView.getChildren().add(grid);
                    }
                }
            } catch (ServiceException e) {
                photoView.getChildren().clear();
                Label lab = new Label();
                lab.setText("Keine Fotos vorhanden");
                photoView.getChildren().add(lab);
            }
        }else{
            photoView.getChildren().clear();
            Label lab = new Label();
            lab.setText("Bitte eine Reise auswählen");
            photoView.getChildren().add(lab);
        }
    }

    /**
     * Returns the radius
     * @param degrees
     * @return the dadius
     */
    public double ToRadians(double degrees) {
        double radians = degrees * Math.PI / 180;
        return radians;
    }

    /**
     * Retruns the distance between 2 LatLong Objekts
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public double DirectDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = ToRadians(lat2-lat1);
        double dLng = ToRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(ToRadians(lat1)) * Math.cos(ToRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        double meterConversion = 1609.00;
        return dist * meterConversion;
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
        fitMarkersToScreen(path, 0, path.length - 1);
        for(int i = 0; i<path.length;i++) {
            Marker m = new Marker(new MarkerOptions().position(path[i]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
            googleMap.addMarker(m);
            markers.add(m);
        }
    }

    private void playTheJourney(int pos) {

        LatLong [] path = toLatLong(places);

        fitMarkersToScreen(path, pos, pos + 1);
        if(actualMarker!=null){
            googleMap.removeMarker(actualMarker);
        }
        Marker m = new Marker(new MarkerOptions().position(path[pos]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
        googleMap.addMarker(m);
        markers.add(m);
        if (pos < path.length - 1) {

            PolylineOptions polylineOptions = new PolylineOptions();
            MVCArray mvcArray = new MVCArray();
            mvcArray.push(path[pos]);
            mvcArray.push(path[pos + 1]);
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
            actualMarker = new Marker(new MarkerOptions().position(path[pos+1]));
            googleMap.addMarker(actualMarker);
        }

    }

    private void fitMarkersToScreen(LatLong[] subpath, int from, int to) {
        Double ne_lat = null;
        Double ne_long = null;
        Double sw_lat = null;
        Double sw_long = null;
        for(int i = from; i<=to && i<subpath.length;i++) {
            if (ne_lat == null) {
                ne_lat = subpath[i].getLatitude();
            }
            if (ne_long == null) {
                ne_long = subpath[i].getLongitude();
            }
            if (sw_lat == null) {
                sw_lat = subpath[i].getLatitude();
            }
            if (sw_long == null) {
                sw_long = subpath[i].getLongitude();
            }
            if (subpath[i].getLatitude() > ne_lat) {
                ne_lat = subpath[i].getLatitude();
            }
            if (subpath[i].getLongitude() > ne_long) {
                ne_long = subpath[i].getLongitude();
            }
            if (subpath[i].getLatitude() < sw_lat) {
                sw_lat = subpath[i].getLatitude();
            }
            if (subpath[i].getLongitude() < sw_long) {
                sw_long = subpath[i].getLongitude();
            }
        }
        LatLong ne = new LatLong(ne_lat,ne_long);
        LatLong sw = new LatLong(sw_lat,sw_long);
        double latFraction = ((ne.latToRadians()) - sw.latToRadians()) / Math.PI;
        double lngDiff = ne.getLongitude() - sw.getLongitude();
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = Math.floor(Math.log(300 / 256 / latFraction) / 0.6931472);
        double lngZoom = Math.floor(Math.log((borderPane.getWidth()-240) / 256 / lngFraction) / 0.6931472);
        double min = Math.min(latZoom, lngZoom);
        min = Math.min(min,21);
        mapsScene.setZoom((int) min);
        mapsScene.setCenter((ne.getLatitude() + sw.getLatitude()) / 2,
                (ne.getLongitude() + sw.getLongitude()) / 2);
    }

    private LatLong[] getDestinations(){
        LatLong[] path={
                new LatLong(48.2363038,16.3478819),
                new LatLong( 48.236299,16.3478708),
                new LatLong(48.232022, 16.376037),
                new LatLong(48.216240, 16.396758),
                new LatLong(48.197900, 16.415591),
                new LatLong(48.189453, 16.403740),
                new LatLong(48.185354, 16.362832),
                new LatLong(48.194362, 16.343365),
                new LatLong(48.215007, 16.338477),
                new LatLong(48.161529, 16.369028)
        };
        return path;
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
        pos = 0;
        if(actualMarker!=null){
            googleMap.removeMarker(actualMarker);
        }
    }

    private LatLong[] toLatLong(List<Place> places){
        LatLong[] path = new LatLong[places.size()];
        for(int i = 0; i<places.size(); i++){
            path[i] = new LatLong(places.get(i).getLatitude(),places.get(i).getLongitude());
        }
        return path;
    }
}
