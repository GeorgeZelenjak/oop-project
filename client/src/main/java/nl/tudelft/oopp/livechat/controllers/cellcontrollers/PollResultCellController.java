package nl.tudelft.oopp.livechat.controllers.cellcontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import nl.tudelft.oopp.livechat.businesslogic.PercentageCalculator;
import nl.tudelft.oopp.livechat.data.PollAndOptions;
import nl.tudelft.oopp.livechat.data.PollOption;

import java.io.IOException;

public class PollResultCellController {

    @FXML
    AnchorPane cellAnchorPane;

    @FXML
    Text resultOptionText;

    @FXML
    Text numberOfVotes;

    @FXML
    Rectangle resultBarRectangle;

    @FXML
    Label answeredTick;


    private PollOption option;

    /**
     * Creates a new Cell data object.
     */
    public PollResultCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/cell/pollResultCell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the anchor pane.
     * @return the anchor pane
     */
    public AnchorPane getBox() {
        return cellAnchorPane;
    }

    /**
     * Sets the poll option.
     * @param option the poll option
     */
    public void setOption(PollOption option) {
        this.option = option;
    }

    /**
     * Sets the option text.
     */
    public void setText() {
        numberOfVotes.setText(option.getVotes() + "");
        resultOptionText.setText(option.getOptionText());
    }

    /**
     * Sets the rectangle to display the votes.
     */
    public void setRectangle() {
        double frac = PercentageCalculator.calculatePercentage(
                (int) PollAndOptions.getCurrent().getPoll().getVotes(),
                (int) option.getVotes());
        resultBarRectangle.setWidth(resultBarRectangle.getWidth() * frac);
    }

    /**
     * Sets the tick if option is correct.
     */
    public void setCorrect() {
        if (option.isCorrect()) {
            answeredTick.setVisible(true);
        }
    }
}
