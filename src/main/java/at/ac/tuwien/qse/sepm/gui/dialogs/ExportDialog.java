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
import at.ac.tuwien.qse.sepm.service.ExportService;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExportDialog extends ResultDialog<String> {

    private final int photoCount;
    private String exportFolder;
    @FXML private Button browseButton;
    @FXML private TextField directoryField;
    @FXML private Button exportButton;
    @FXML private Button cancelButton;
    @FXML private Button dropboxButton;
    @FXML private Label statusText;

    private Node root;

    private ExportService exportService;

    /**
     * {@inheritDoc}
     */
    public ExportDialog(Node origin, ExportService exportService, int photoCount) {
        super(origin, "Fotos exportieren");
        FXMLLoadHelper.load(this, this, ExportDialog.class, "view/ExportDialog.fxml");

        this.photoCount = photoCount;
        this.exportService = exportService;
        this.root = origin;

        browseButton.setOnAction(this::handleBrowse);
        exportButton.setOnAction(this::handleExport);
        cancelButton.setOnAction(this::handleCancel);
        dropboxButton.setOnAction(this::handleDropBox);

        updateStatus();
        directoryField.textProperty()
                .addListener((observable, oldValue, newValue) -> updateStatus());
    }

    private void handleDropBox(Event event) {
        String dropboxFolder = exportService.getDropboxFolder();

        if (dropboxFolder != null) {
            this.exportFolder = dropboxFolder;
            directoryField.setText(dropboxFolder);
        }

        handleBrowse(event);
    }

    private void handleBrowse(Event event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Export Ort wählen");
        if (exportFolder != null) {
            directoryChooser.setInitialDirectory(new File(exportFolder));
        }
        File directory = directoryChooser.showDialog(null);

        if (directory == null || !directory.exists()) {
            getLogger().info("Invalid directory selected.");
            return;
        }

        directoryField.setText(directory.getPath());
        exportFolder = directory.getPath();
    }

    private void handleExport(Event event) {
        Path root = Paths.get(exportFolder);
        Path dest = Paths.get(directoryField.getText());

        Path relative = root.relativize(dest);

        setResult(exportFolder);

        close();
    }

    private void handleCancel(Event event) {
        close();
    }

    private void updateStatus() {

        exportButton.setDisable(true);

        String path = directoryField.getText();
        if (path.isEmpty()) {
            statusText.setText("Kein Ordner angegeben");
            return;
        }

        File directory = new File(path);
        if (!directory.exists()) {
            statusText.setText("Ordner nicht gefunden");
            return;
        }

        // Export directory is valid.
        exportButton.setDisable(false);
        String format = "%d Fotos ausgewählt";
        if (photoCount == 1) {
            format = "%d Foto ausgewählt";
        }
        statusText.setText(String.format(format, photoCount));
    }
}
