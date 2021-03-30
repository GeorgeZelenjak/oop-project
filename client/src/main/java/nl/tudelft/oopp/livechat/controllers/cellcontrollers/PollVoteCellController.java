package nl.tudelft.oopp.livechat.controllers.cellcontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import nl.tudelft.oopp.livechat.controllers.popupcontrollers.PollingManagementPopupController;
import nl.tudelft.oopp.livechat.data.PollOption;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.PollCommunication;

import java.io.IOException;

public class PollVoteCellController {

    @FXML
    private AnchorPane cellAnchorPane;

    @FXML
    private Text optionText;

    @FXML
    private ToggleButton answerButton;

    private PollOption option;

    /**
     * Creates a new Cell data object.
     */
    public PollVoteCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/cell/pollVoteCell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setOption(PollOption option) {
        this.option = option;
    }

    public void setText() {
        optionText.setText(option.getOptionText());
    }

    public AnchorPane getBox() {
        return cellAnchorPane;
    }

    /**
     * Sets vote button.
     */
    public void setVoteButton() {
        answerButton.setOnAction((ActionEvent event) -> {
            System.out.println(option.getId());
            PollCommunication.vote(User.getUid(), option.getId());
            cellAnchorPane.getScene().getWindow().hide();
        });
    }
}
