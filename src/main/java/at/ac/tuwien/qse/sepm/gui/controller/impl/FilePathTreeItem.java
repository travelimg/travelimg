package at.ac.tuwien.qse.sepm.gui.controller.impl;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class FilePathTreeItem extends TreeItem<String> {
    public FontAwesomeIconView folderIconOpen = new FontAwesomeIconView(
            FontAwesomeIcon.FOLDER_OPEN_ALT);
    public FontAwesomeIconView folderIconClosed = new FontAwesomeIconView(
            FontAwesomeIcon.FOLDER_ALT);

    //this stores the full path to the file or directory
    private String fullPath;

    public String getFullPath() {
        return (this.fullPath);
    }

    private boolean isDirectory;

    public boolean isDirectory() {
        return (this.isDirectory);
    }

    public FilePathTreeItem(Path file) {
        super(file.toString());
        this.fullPath = file.toString();

        //test if this is a directory and set the icon
        if (Files.isDirectory(file)) {
            this.isDirectory = true;
            this.setGraphic(folderIconOpen);
        }

        //set the value
        if (!fullPath.endsWith(File.separator)) {
            //set the value (which is what is displayed in the tree)
            String value = file.toString();
            int indexOf = value.lastIndexOf(File.separator);
            if (indexOf > 0) {
                this.setValue(value.substring(indexOf + 1));
            } else {
                this.setValue(value);
            }
        }

        this.addEventHandler(TreeItem.<Object>branchExpandedEvent(), new EventHandler() {
            @Override public void handle(Event e) {
                FilePathTreeItem source = (FilePathTreeItem) e.getSource();
                if (source.isDirectory() && source.isExpanded()) {
                    FontAwesomeIconView icon = new FontAwesomeIconView(
                            FontAwesomeIcon.FOLDER_OPEN_ALT);
                    source.setGraphic(icon);
                }
                try {
                    if (source.getChildren().isEmpty()) {
                        Path path = Paths.get(source.getFullPath());
                        BasicFileAttributes attribs = Files
                                .readAttributes(path, BasicFileAttributes.class);
                        if (attribs.isDirectory()) {
                            DirectoryStream<Path> dir = Files.newDirectoryStream(path);
                            for (Path file : dir) {
                                FilePathTreeItem treeNode = new FilePathTreeItem(file);
                                source.getChildren().add(treeNode);
                            }
                        }
                    }
                } catch (IOException x) {
                    x.printStackTrace();
                }
            }
        });

        this.addEventHandler(TreeItem.<Object>branchCollapsedEvent(), new EventHandler() {
            @Override public void handle(Event e) {
                FilePathTreeItem source = (FilePathTreeItem) e.getSource();
                if (source.isDirectory() && !source.isExpanded()) {
                    FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.FOLDER_ALT);
                    source.setGraphic(icon);
                }
            }
        });

    }
}
