package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.WikiPlaceInfo;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.WikipediaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class WikipediaInfoPane extends Pane {

    @FXML
    private Label placeLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TableColumn<String, String> categoryName;
    @FXML
    private TableColumn<String, String> categoryValue;
    @FXML
    private TableView<Pair<String, String>> infoTable;

    @Autowired
    WikipediaService wikipediaService;


    public void initialize() {

        categoryName.setCellValueFactory(new PropertyValueFactory<>("Kategorie"));
        categoryValue.setCellValueFactory(new PropertyValueFactory<>("Wert"));

        showDefaultWikiInfo();
    }


    /**
     * Clear the info table and reset the labels to default values.
     */
    public void showDefaultWikiInfo() {
        placeLabel.setText("Stadt");
        countryLabel.setText("Land");
        descriptionLabel.setText("Your ad here.");

        infoTable.getColumns().clear();
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
            info = new WikiPlaceInfo(place.getCity(), place.getCountry(), "Fehler beim Laden des Wikipedia Infos",
                    null, null, null, null, null, null);
        }

        placeLabel.setText(info.getPlaceName());
        countryLabel.setText(info.getCountryName());
        descriptionLabel.setText(info.getDescription());

        List<Pair<String, String>> infoList = new ArrayList<Pair<String, String>>();

        if (info.getPopulation() != null) {
            infoList.add(new Pair<String, String>("Einwohner", info.getPopulation().toString()));
        }
        if (info.getArea() != null) {
            infoList.add(new Pair<String, String>("Fläche", info.getArea().toString() + "m²"));
        }
        if (info.getElevation() != null) {
            infoList.add(new Pair<String, String>("Höhe", info.getElevation().toString() + "m²"));
        }
        if (info.getUtcOffset() != null) {
            infoList.add(new Pair<String, String>("Zeitzone", info.getUtcOffset().toString()));
        }
        if (info.getCurrency() != null) {
            infoList.add(new Pair<String, String>("Währung", info.getCurrency().toString()));
        }
        if (info.getLanguage() != null) {
            infoList.add(new Pair<String, String>("Sprache(n)", info.getLanguage()));
        }

        ObservableList<Pair<String, String>> exifData = FXCollections.observableArrayList(infoList);
        infoTable.setItems(exifData);
    }
}