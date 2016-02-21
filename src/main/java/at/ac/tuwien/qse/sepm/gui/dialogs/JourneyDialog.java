package at.ac.tuwien.qse.sepm.gui.dialogs;

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

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JourneyDialog extends ResultDialog<Journey> {

    private static final Logger logger = LogManager.getLogger(JourneyDialog.class);

    private final ClusterService clusterService;

    @FXML
    private Button journeyAdd;
    @FXML
    private TextField journeyNameField;
    @FXML
    private DatePicker journeyEndDate;
    @FXML
    private Button journeyCancel;
    @FXML
    private DatePicker journeyBeginDate;

    public JourneyDialog(Node origin, ClusterService clusterService) {
        super(origin, "Reise hinzufügen");
        FXMLLoadHelper.load(this, this, JourneyDialog.class, "view/JourneyDialog.fxml");

        this.clusterService = clusterService;

        journeyAdd.setOnAction(this::handleAdd);
        journeyCancel.setOnAction(this::handleCancel);

        journeyNameField.textProperty().addListener(observable -> updateStatus());
        journeyBeginDate.valueProperty().addListener(observable -> updateStatus());
        journeyEndDate.valueProperty().addListener(observable -> updateStatus());
    }

    private void handleCancel(ActionEvent actionEvent) {
        close();
    }

    private void handleAdd(ActionEvent actionEvent) {
        Journey journey = new Journey(-1, journeyNameField.getText(), journeyBeginDate.getValue().atStartOfDay(), journeyEndDate.getValue().atStartOfDay());
        try {
            clusterService.clusterJourney(journey);
        } catch (ServiceException ex) {
            logger.error("Failed to cluster journey", ex);
            ErrorDialog.show(this, "Fehler beim Erstellen der Reise", "Fehlermeldung: " + ex.getMessage());
        }

        setResult(journey);
        close();
    }

    private void updateStatus() {
        journeyAdd.setDisable(
                journeyNameField.getText().isEmpty()
                        || journeyBeginDate.getValue() == null
                        || journeyEndDate.getValue() == null
        );
    }
}
