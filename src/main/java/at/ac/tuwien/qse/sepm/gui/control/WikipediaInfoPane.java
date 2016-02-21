package at.ac.tuwien.qse.sepm.gui.control;


/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.WikiPlaceInfo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.util.BackgroundTask;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class WikipediaInfoPane extends VBox {

    @FXML
    private VBox root;
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

        infoTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


    /**
     * Clear the info table and reset the labels to default values.
     */
    public void showDefaultWikiInfo(Place place) {
        placeLabel.setText("");
        countryLabel.setText("");
        descriptionText.setText("");

        infoTable.getItems().clear();

        showWikipediaInfo(place);
    }

    /**
     * Display Wikipedia info for the given place in the pane.
     * @param place should be a valid instance
     */
    public void showWikipediaInfo(Place place) {

        BackgroundTask<WikiPlaceInfo> task = new BackgroundTask<WikiPlaceInfo>() {
            @Override
            public WikiPlaceInfo compute() throws ComputeException {
                try {
                    return wikipediaService.getWikiPlaceInfo(place);
                } catch (ServiceException ex) {
                    return new WikiPlaceInfo(place.getCity(), place.getCountry(),
                            "Fehler beim Laden des Wikipedia Infos", null, null, null, null, null, null);
                }
            }

            @Override
            public void onFinished(WikiPlaceInfo result) {
                updatePane(result);
            }
        };

        (new Thread(task)).start();
    }

    public void updatePane(WikiPlaceInfo info) {
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