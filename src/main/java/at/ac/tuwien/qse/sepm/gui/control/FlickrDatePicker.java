package at.ac.tuwien.qse.sepm.gui.control;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A simple datepicker for flickr. Ranges of the dates can be limited.
 */
public class FlickrDatePicker extends DatePicker {

    private static final Logger logger = LogManager.getLogger();

    public FlickrDatePicker(){
        super();
    }

    public void setRanges(LocalDateTime startDate, LocalDateTime endDate){
        logger.debug("Setting allowed dates from {} to {}", startDate, endDate);
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(startDate.toLocalDate()) || item.isAfter(endDate.toLocalDate())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        setDayCellFactory(dayCellFactory);
    }
}
