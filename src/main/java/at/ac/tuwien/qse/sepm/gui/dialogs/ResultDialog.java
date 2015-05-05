package at.ac.tuwien.qse.sepm.gui.dialogs;

import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;

/**
 * Dialog which returns a result (e.g prompting for input)
 */
public abstract class ResultDialog<R> extends BaseDialog {

    private Optional<R> result = Optional.empty();

    public ResultDialog(URL fxml, Stage parent) {
        super(fxml, parent);
    }

    public Optional<R> run() {
        showAndWait();
        return result;
    }

    public void setResult(R value) {
        result = Optional.of(value);
    }
}
