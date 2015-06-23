package at.ac.tuwien.qse.sepm.gui.control;


import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.WikiPlaceInfo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.WikipediaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class WikipediaInfoPane extends VBox {

    @FXML
    private Label placeLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Text descriptionText;
    @FXML
    private TableColumn<String, String> categoryName;
    @FXML
    private TableColumn<String, String> categoryValue;
    @FXML
    private TableView<Pair<String, String>> infoTable;

    private WikipediaService wikipediaService;

    public WikipediaInfoPane(WikipediaService wikipediaService) {
        this.wikipediaService = wikipediaService;
        FXMLLoadHelper.load(this, this, WikipediaInfoPane.class, "view/WikipediaInfoPane.fxml");
    }

    @FXML
    private void initialize() {

        categoryName.setCellValueFactory(new PropertyValueFactory<>("Key"));
        categoryValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
    }


    /**
     * Clear the info table and reset the labels to default values.
     */
    public void showDefaultWikiInfo(Place place) {
        placeLabel.setText("Stadt");
        countryLabel.setText("Land");
        descriptionText.setText("Your ad here.");

        infoTable.getItems().clear();

        showWikipediaInfo(place);
    }

    /**
     * Display Wikipedia info for the given place in the pane.
     * @param place should be a valid instance
     */
    public void showWikipediaInfo(Place place) {

        WikiPlaceInfo info;

        try {
            info = wikipediaService.getWikiPlaceInfo(place);

        } catch (ServiceException ex) {
            info = new WikiPlaceInfo(place.getCity(), place.getCountry(),
                    "Fehler beim Laden des Wikipedia Infos", null, null, null, null, null, null);
        }

        placeLabel.setText(info.getPlaceName());
        countryLabel.setText(info.getCountryName());
        descriptionText.setText(info.getDescription());

        List<Pair<String, String>> infoList = new ArrayList<Pair<String, String>>();

        if (info.getPopulation() != null) {
            infoList.add(new Pair<String, String>("Bevölkerung", info.getPopulation().toString()));
        }
        if (info.getArea() != null) {
            infoList.add(new Pair<String, String>("Fläche", info.getArea().toString() + "km²"));
        }
        if (info.getElevation() != null) {
            infoList.add(new Pair<String, String>("Höhe", info.getElevation().toString() + "m"));
        }
        if (info.getUtcOffset() != null) {
            infoList.add(new Pair<String, String>("Zeitzone", info.getUtcOffset()));
        }
        if (info.getCurrency() != null) {
            infoList.add(new Pair<String, String>("Währung", info.getCurrency()));
        }
        if (info.getLanguage() != null) {
            infoList.add(new Pair<String, String>("Sprache(n)", info.getLanguage()));
        }

        ObservableList<Pair<String, String>> obsInfoList =
                FXCollections.observableArrayList(infoList);
        infoTable.setItems(obsInfoList);
    }
}