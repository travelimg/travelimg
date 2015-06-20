package at.ac.tuwien.qse.sepm.gui.control;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class PageSelector extends HBox {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Button previousButton = new Button();
    private final Button nextButton = new Button();
    private final HBox buttonContainer = new HBox();
    private final List<PageButton> buttons = new LinkedList<>();

    public PageSelector() {
        LOGGER.debug("instantiating");
        getStyleClass().add("page-selector");

        FontAwesomeIconView previousIcon = new FontAwesomeIconView();
        previousIcon.setGlyphName("CHEVRON_LEFT");
        previousButton.setGraphic(previousIcon);
        Tooltip previousTooltip = new Tooltip();
        previousTooltip.setText("Vorige Seite");
        previousButton.setTooltip(previousTooltip);
        previousButton.getStyleClass().addAll("previous");
        previousButton.setOnAction(e -> previousPage());
        previousButton.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(previousButton, Priority.ALWAYS);

        FontAwesomeIconView nextIcon = new FontAwesomeIconView();
        nextIcon.setGlyphName("CHEVRON_RIGHT");
        nextButton.setGraphic(nextIcon);
        Tooltip nextTooltip = new Tooltip();
        nextTooltip.setText("Vorige Seite");
        nextButton.setTooltip(nextTooltip);
        nextButton.getStyleClass().addAll("previous");
        nextButton.setOnAction(e -> nextPage());
        nextButton.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(nextButton, Priority.ALWAYS);

        getChildren().addAll(previousButton, buttonContainer, nextButton);
        setPageCount(1);
        setCurrentPage(0);
    }

    public IntegerProperty pageCountProperty() {
        return pageCountProperty;
    }
    private final IntegerProperty pageCountProperty = new SimpleIntegerProperty();
    public int getPageCount() {
        return pageCountProperty().get();
    }
    public void setPageCount(int pageCount) {
        LOGGER.debug("setting page count from {} to {}", getPageCount(), pageCount);
        if (pageCount < 1) throw new IllegalArgumentException();
        if (getPageCount() == pageCount) return;
        pageCountProperty().set(pageCount);
        updatePageCount();
    }

    public IntegerProperty currentPageProperty() {
        return currentPageProperty;
    }
    private final IntegerProperty currentPageProperty = new SimpleIntegerProperty();
    public int getCurrentPage() {
        return currentPageProperty.get();
    }
    public void setCurrentPage(int currentPage) {
        LOGGER.debug("setting current page from {} to {}", getCurrentPage(), currentPage);
        if (currentPage < 0) throw new IndexOutOfBoundsException();
        if (currentPage >= getPageCount()) throw new IndexOutOfBoundsException();

        buttons.forEach(b -> b.setSelected(false));
        currentPageProperty().set(currentPage);
        buttons.get(getCurrentPage()).setSelected(true);

        previousButton.setDisable(false);
        nextButton.setDisable(false);
        if (isFirstPage()) {
            LOGGER.debug("current page is first page");
            previousButton.setDisable(true);
        }
        if (isLastPage()) {
            LOGGER.debug("current page is last page");
            nextButton.setDisable(true);
        }
    }

    public int getLastPage() {
        return getPageCount() - 1;
    }

    public boolean isFirstPage() {
        return getCurrentPage() == 0;
    }

    public boolean isLastPage() {
        return getCurrentPage() == getLastPage();
    }

    public void nextPage() {
        if (isLastPage()) return;
        LOGGER.debug("switching to next page from {}", getCurrentPage());
        setCurrentPage(getCurrentPage() + 1);
    }

    public void previousPage() {
        if (isFirstPage()) return;
        LOGGER.debug("switching to previous page from {}", getCurrentPage());
        setCurrentPage(getCurrentPage() - 1);
    }

    private void updatePageCount() {

        // NOTE: No pagination needed when there is only one page.
        boolean multiplePages = getPageCount() > 1;
        setVisible(multiplePages);
        setManaged(multiplePages);

        buttons.clear();
        buttonContainer.getChildren().clear();
        for (int i = 0; i < getPageCount(); i++) {
            PageButton button = new PageButton();
            button.setMaxHeight(Double.MAX_VALUE);
            HBox.setHgrow(button, Priority.ALWAYS);
            buttons.add(button);
            buttonContainer.getChildren().add(button);
            button.setOnAction(e -> handleClick(e.getSource()));
        }
        if (getCurrentPage() > getLastPage()) {
            setCurrentPage(getLastPage());
        }
        buttons.get(getCurrentPage()).setSelected(true);
    }

    private void handleClick(Object button) {
        int index = buttons.indexOf(button);
        if (index < 0) {
            LOGGER.warn("received click event from unknown page button");
            return;
        }
        if (index > getLastPage()) {
            LOGGER.warn("button list size {} is too long for the page count of {}", buttons.size(), getPageCount());
            return;
        }
        setCurrentPage(index);
    }

    private static class PageButton extends Button {

        private final FontAwesomeIconView icon = new FontAwesomeIconView();

        public PageButton() {
            getStyleClass().add("page-button");
            setGraphic(icon);
            icon.setGlyphName("CIRCLE");
            setSelected(false);
        }

        public BooleanProperty selectedProperty() {
            return selectedProperty;
        }
        private final BooleanProperty selectedProperty = new SimpleBooleanProperty();
        public boolean isSelected() {
            return selectedProperty().get();
        }
        public void setSelected(boolean selected) {
            selectedProperty().set(selected);
            update();
        }

        private void update() {
            if (isSelected()) {
                getStyleClass().add("selected");
            } else {
                getStyleClass().removeAll("selected");
            }
        }
    }
}
