package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Journey;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A simple combobox for flickr (contains Journey objects)
 */
public class FlickrJourneysComboBox extends ComboBox {

    private static final Logger logger = LogManager.getLogger();

    public FlickrJourneysComboBox(){
        super();
        setCellFactory(new Callback<ListView<Journey>, ListCell<Journey>>() {
            @Override public ListCell<Journey> call(ListView<Journey> l) {
                return new ListCell<Journey>() {
                    @Override protected void updateItem(Journey j, boolean empty) {
                        super.updateItem(j, empty);
                        if (j != null) {
                            setText(j.getName());
                        }
                    }
                };
            }
        });
        setConverter(new StringConverter<Journey>() {
            @Override public String toString(Journey journey) {
                if (journey == null) {
                    return null;
                } else {
                    return journey.getName();
                }
            }

            @Override public Journey fromString(String string) {
                return null;
            }
        });
    }

    /**
     * If a new journey is chosen from the ComboBox, the FlickrDatePicker will be updated with these journey's valid dates.
     * @param flickrDatePicker
     */
    public void bindToFlickrDatePicker(FlickrDatePicker flickrDatePicker){
        valueProperty().addListener(new ChangeListener<Journey>() {
            @Override public void changed(ObservableValue ov, Journey oldValue, Journey newValue) {
                if(newValue!=null && newValue.getId()!=-1){
                    flickrDatePicker.setRanges(newValue.getStartDate(), newValue.getEndDate());
                    flickrDatePicker.setValue(newValue.getStartDate().toLocalDate());
                    logger.debug("Date set to {}", newValue.getStartDate().toLocalDate());
                }
                else if(newValue!=null && newValue.getId()==-1){
                    flickrDatePicker.setRanges(LocalDateTime.MIN,LocalDateTime.MAX);
                    flickrDatePicker.setValue(LocalDate.now());
                    logger.debug("Today's date set");
                }
            }
        });
    }

    public Journey getSelectedJourney(){
        return (Journey) getSelectionModel().getSelectedItem();
    }

}


