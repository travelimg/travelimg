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

import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Dialog that provides the user with a short message.
 */
public class InfoDialog extends Dialog {

    @FXML
    private Node root;

    @FXML
    private Label headerText;

    @FXML
    private Label contentText;

    @FXML
    private Button cancelButton;

    /**
     * {@inheritDoc}
     */
    public InfoDialog(Node origin, String title) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, InfoDialog.class, "view/InfoDialog.fxml");

        cancelButton.setOnAction(this::handleCancel);
    }

    /**
     * Text that is displayed in the header bar.
     * <p>
     * It should summarize the reason for the dialog in a few words.
     */
    public String getHeaderText() {
        return headerTextProperty().get();
    }

    public void setHeaderText(String headerText) {
        headerTextProperty().set(headerText);
    }

    public StringProperty headerTextProperty() {
        return headerText.textProperty();
    }

    /**
     * Text that is displayed in the body. This is usually the full error message.
     */
    public String getContentText() {
        return contentTextProperty().get();
    }

    public void setContentText(String contentText) {
        contentTextProperty().set(contentText);
    }

    public StringProperty contentTextProperty() {
        return contentText.textProperty();
    }

    /**
     * Value indicating that this dialog notifies about an error.
     */
    public boolean isError() {
        return root.getStyleClass().contains("error");
    }

    public void setError(boolean isError) {
        if (isError) {
            root.getStyleClass().add("error");
        } else {
            root.getStyleClass().remove("error");

        }
    }

    private void handleCancel(Event event) {
        close();
    }
}
