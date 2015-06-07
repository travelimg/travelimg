package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.*;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HighlightsViewController {

    @FXML private BorderPane borderPane;
    @FXML private GoogleMapsScene mapsScene;
    @FXML private VBox journeys, mapContainer, photoView;
    @FXML private FilterList<Journey> journeyListView;
    @FXML private ScrollPane scrollPhotoView;
    @Autowired private ClusterService clusterService;
    @Autowired private PhotoService photoService;
    @Autowired private PhotoFilter filter;
    @Autowired private TagService tagService;
    private GoogleMapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private ArrayList<Polyline> polylines = new ArrayList<Polyline>();
    private List<Place> places;
    private HashMap<RadioButton,Journey> journeyRadioButtonsHashMap = new HashMap<>();
    private Marker actualMarker;
    private boolean disableReload = false;
    private Consumer<PhotoFilter> filterChangeCallback;
    private final ImageGrid<Photo> grid = new ImageGrid<>(PhotoGridTile::new);
    private int pos = 0;
    private static final Logger LOGGER = LogManager.getLogger();

    public void initialize(){
        reloadJourneys();
        /**to remove - BEGIN**/
        HBox vBox = new HBox();
        Button playButton = new Button("play!");
        playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if(pos==0){
                    clearMap();
                }
                playTheJourney(pos);
                pos++;
            }
        });
        vBox.getChildren().add(playButton);
        borderPane.setBottom(vBox);
        /**to remove - END**/
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
        LOGGER.debug("Selected journey {}",j.getName());
        try {
            places = clusterService.getPlacesByJourney(j);
            drawDestinationsAsPolyline(toLatLong(places));
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
    private void reloadImages(){

        try{

            List<Photo> allPhotos = photoService.getAllPhotos(filter)
                    .stream()
                    .sorted((p1, p2) -> p2.getDatetime().compareTo(p1.getDatetime()))
                    .collect(Collectors.toList());
            List<Photo> goodPhotos = new ArrayList<>();
            for(Photo p : allPhotos){
                if(p.getRating()==(Rating.GOOD)){
                    goodPhotos.add(p);
                }
            }
            if(goodPhotos.size()==0){
                photoView.getChildren().clear();
                Label lab = new Label();
                lab.setText("Es sind kein mit 'good' bewerteten Fotos zu dieser Reise vorhanden");
                photoView.getChildren().add(lab);

            }else {
                try {
                    List<Tag> taglist = tagService.getMostFrequentTags(goodPhotos);
                    photoView.getChildren().clear();
                    for (Tag t : taglist) {
                        List<Photo> name = new ArrayList<>();
                        int counter = 0;
                        for (Photo p : goodPhotos) {
                            for (Tag t2 : p.getTags()) {
                                if (t.getId() == t2.getId() && counter < 5) {
                                    name.add(p);
                                    counter++;
                                }
                            }
                        }
                        ImageGrid<Photo> grid2 = new ImageGrid<>(PhotoGridTile::new);
                        grid2.setItems(name);
                        TitledPane tp = new TitledPane(t.getName(), grid2);

                        photoView.getChildren().add(tp);
                    }
                } catch (ServiceException e) {
                    LOGGER.debug("Photos habe keine Tag's ", e);
                    photoView.getChildren().clear();
                    grid.setItems(goodPhotos);
                    photoView.getChildren().add(grid);
                }
            }
            }catch (ServiceException e){
            photoView.getChildren().clear();
            Label lab = new Label();
            lab.setText("Keine Fotos vorhanden");
            photoView.getChildren().add(lab);
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
