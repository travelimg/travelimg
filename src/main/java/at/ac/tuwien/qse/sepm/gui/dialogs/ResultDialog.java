package at.ac.tuwien.qse.sepm.gui.dialogs;

import javafx.scene.Node;

import java.util.Optional;

/**
 * Dialog which returns a result.
 */
public abstract class ResultDialog<R> extends Dialog {

    private Optional<R> result = Optional.empty();

    public ResultDialog(Node origin, String title) {
        super(origin, title);
    }

    public Optional<R> showForResult() {
        showAndWait();
        return result;
    }

    public void setResult(R result) {
        this.result = Optional.of(result);
    }
}
