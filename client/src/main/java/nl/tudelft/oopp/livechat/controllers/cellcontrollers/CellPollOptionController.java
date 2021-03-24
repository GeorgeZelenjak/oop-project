package nl.tudelft.oopp.livechat.controllers.cellcontrollers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import nl.tudelft.oopp.livechat.data.PollOption;

import java.io.IOException;

public class CellPollOptionController {

    @FXML
    private TextField optionTextField;

    @FXML
    private CheckBox pollOptionCellIsCorrectCheckBox;

    @FXML
    private AnchorPane optionCellAnchorPane;

    private PollOption option;

    /**
     * Instantiates a new Cell data poll option.
     */
    public CellPollOptionController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/cell/pollOptionCell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Sets listener.
     */
    public void setListener() {
        optionTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean oldPropertyValue, Boolean newPropertyValue) {
                option.setOptionText(optionTextField.getText());
            }
        });
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
        if (value != null) {
            optionTextField.setText(value);
        }
    }

    /**
     * Gets box.
     * @return the box
     */
    public AnchorPane getBox() {
        return optionCellAnchorPane;
    }
}
