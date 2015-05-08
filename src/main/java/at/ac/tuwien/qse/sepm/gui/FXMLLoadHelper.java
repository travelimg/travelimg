package at.ac.tuwien.qse.sepm.gui;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * Helper for loading FXML files.
 */
public class FXMLLoadHelper {

    /**
     * Loads an FXML structure for a controller.
     *
     * @param root root node of the control
     * @param controller controller object
     * @param clazz type of the control
     * @param location path of FXML file
     */
    public static void load(Object root, Object controller, Class clazz, String location) {
        FXMLLoader fxmlLoader = new FXMLLoader(clazz.getClassLoader().getResource(
            location));
        fxmlLoader.setRoot(root);
        fxmlLoader.setController(controller);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
