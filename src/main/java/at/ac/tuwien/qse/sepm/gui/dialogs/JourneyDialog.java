package at.ac.tuwien.qse.sepm.gui.dialogs;

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

/**
 * Created by David on 21.05.2015.
 */
public class JourneyDialog extends ResultDialog<Journey>{

    private static final Logger logger = LogManager.getLogger(JourneyDialog.class);

    private final ClusterService clusterService;

    @FXML private Button journeyAdd;

    @FXML private TextField journeyNameField;

    @FXML private DatePicker journeyEndDate;

    @FXML private Button journeyCancel;

    @FXML private DatePicker journeyBeginDate;


    public JourneyDialog(Node origin, ClusterService clusterService) {
        super(origin, "Reise hinzufügen");
        logger.debug("new Journey Dialog");
        FXMLLoadHelper.load(this, this, JourneyDialog.class, "view/JourneyDialog.fxml");

        this.clusterService = clusterService;

        journeyAdd.setOnAction(this::handleAdd);
        journeyCancel.setOnAction(this::handleCancel);
    }

    private void handleCancel(ActionEvent actionEvent) {
        close();
    }

    private void handleAdd(ActionEvent actionEvent) {
        Journey journey = new Journey(-1, journeyNameField.getText(), journeyBeginDate.getValue().atStartOfDay(), journeyEndDate.getValue().atStartOfDay());
        try {
            clusterService.clusterJourney(journey);
        } catch (ServiceException e) {
            e.printStackTrace();
            // TODO: Show Error Dialog
        }

        //setResult(journey);
        close();
    }
}
