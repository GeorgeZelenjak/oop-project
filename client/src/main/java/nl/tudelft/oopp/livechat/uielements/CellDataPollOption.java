package nl.tudelft.oopp.livechat.uielements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Poll;
import nl.tudelft.oopp.livechat.data.PollOption;
import nl.tudelft.oopp.livechat.data.Question;

import java.io.IOException;

public class CellDataPollOption {

    @FXML
    private TextField optionText;

    @FXML
    private CheckBox pollOptionCellIsCorrectCheckBox;

    @FXML
    private AnchorPane optionCellAnchorPane;

    private PollOption option;

    /**
     * Instantiates a new Cell data poll option.
     */
    public CellDataPollOption() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/pollOptionCell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets poll option cell is correct check box.
     */
    public void setPollOptionCellIsCorrectCheckBox() {
        pollOptionCellIsCorrectCheckBox.setSelected(option.isCorrect());
    }

    /**
     * Change poll option cell is correct check box.
     */
    public void changePollOptionCellIsCorrectCheckBox() {
        pollOptionCellIsCorrectCheckBox.setOnAction((ActionEvent event) -> {
            option.setCorrect(pollOptionCellIsCorrectCheckBox.isSelected());
        });
    }

    /**
     * Sets the question.
     * @param option the question
     */
    public void setPollOption(PollOption option) {
        this.option = option;
    }

    /**
     * Sets option text.
     * @param value the value
     */
    public void setOptionText(String value) {
        optionText.setText("Answer: " + value);
    }

    /**
     * Gets box.
     * @return the box
     */
    public AnchorPane getBox() {
        return optionCellAnchorPane;
    }
}
