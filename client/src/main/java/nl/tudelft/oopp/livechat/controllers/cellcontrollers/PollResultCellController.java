package nl.tudelft.oopp.livechat.controllers.cellcontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    public AnchorPane getBox() {
        return cellAnchorPane;
    }

    public void setOption(PollOption option) {
        this.option = option;
    }

    public void setText() {
        numberOfVotes.setText(option.getVotes() + "");
        resultOptionText.setText(option.getOptionText());
    }

    /**
     * Sets rectangle.
     */
    public void setRectangle() {
        double frac = PercentageCalculator.calculatePercentage(
                (int) PollAndOptions.getCurrentPollAndOptions().getPoll().getVotes(),
                (int) option.getVotes());
        resultBarRectangle.setWidth(resultBarRectangle.getWidth() * frac);
    }
}
