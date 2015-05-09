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

    /**
     * Opens the dialog and returns the result, once the dialog is closed.
     *
     * @return result of the dialog
     */
    public Optional<R> showForResult() {
        showAndWait();
        return result;
    }

    /**
     * Sets the result that is returned by {@link ResultDialog::showForResult} once the dialog
     * is closed.
     *
     * @param result result that should be used
     */
    public void setResult(R result) {
        this.result = Optional.of(result);
    }
}
