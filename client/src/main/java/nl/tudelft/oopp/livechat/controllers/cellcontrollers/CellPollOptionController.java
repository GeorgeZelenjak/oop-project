package nl.tudelft.oopp.livechat.controllers.cellcontrollers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import nl.tudelft.oopp.livechat.controllers.popupcontrollers.PollingManagementPopupController;
import nl.tudelft.oopp.livechat.data.PollOption;

import java.io.IOException;

public class CellPollOptionController {

    @FXML
    private TextField optionTextField;

    @FXML
    private CheckBox pollOptionCellIsCorrectCheckBox;

    @FXML
    private AnchorPane optionCellAnchorPane;

    @FXML
    private Button deleteButton;

    private PollOption option;

    /**
     * Creates a new Cell data poll option.
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
     * Sets a listener for options.
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
     * Sets the delete button.
     */
    public void setDeleteButton() {
        deleteButton.setOnAction((ActionEvent event) -> {
            PollingManagementPopupController.getInEditingOptions().remove(option);
        });
    }

    /**
     * Sets a poll option cell "is correct" check box.
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
     * Sets the poll option.
     * @param option the poll option
     */
    public void setPollOption(PollOption option) {
        this.option = option;
    }

    /**
     * Sets the option text.
     * @param value the option text
     */
    public void setOptionText(String value) {
        if (value != null) {
            optionTextField.setText(value);
        }
    }

    /**
     * Sets the type of the poll.
     */
    public void setPollType() {
        if (PollingManagementPopupController.getAllCorrect()) {
            pollOptionCellIsCorrectCheckBox.setDisable(true);
            pollOptionCellIsCorrectCheckBox.setVisible(false);
        }
    }

    /**
     * Gets the anchor pane.
     * @return the anchor pane
     */
    public AnchorPane getBox() {
        return optionCellAnchorPane;
    }
}
