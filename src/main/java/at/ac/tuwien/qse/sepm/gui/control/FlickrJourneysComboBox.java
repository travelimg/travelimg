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


