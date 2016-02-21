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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ConfirmationDialog extends ResultDialog<Boolean> {

    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Label descriptionLabel;

    /**
     * {@inheritDoc}
     */
    public ConfirmationDialog(Node origin, String title, String description) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, InfoDialog.class, "view/ConfirmationDialog.fxml");
        descriptionLabel.setText(description);
        confirmButton.setOnAction(this::handleConfirm);
        cancelButton.setOnAction(this::handleCancel);
    }

    private void handleCancel(Event event) {
        setResult(false);
        close();
    }

    private void handleConfirm(Event event) {
        setResult(true);
        close();
    }

    public void setConfirmButtonText(String text) {
        confirmButton.setText(text);
    }

    public void setCancelButtonText(String text) {
        cancelButton.setText(text);
    }
}
