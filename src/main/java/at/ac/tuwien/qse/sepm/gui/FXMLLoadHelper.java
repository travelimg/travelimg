package at.ac.tuwien.qse.sepm.gui;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class FXMLLoadHelper {

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
