package nl.tudelft.oopp.livechat.data;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class CellData {

    @FXML
    private Text questionText;

    @FXML
    private AnchorPane questionBoxAnchorPane;

    @FXML
    private Button upvoteButton;

    @FXML
    private Text questionOwner;

    private Question question;

    /**
     * Instantiates a new Cell data.
     */
    public CellData() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/questionCell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(String string) {
        questionText.setText(string);
    }

    public void setOwnerName(String owner) { questionOwner.setText(owner); }

    public AnchorPane getBox() {
        return questionBoxAnchorPane;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    /**
     * Sets upvote button.
     */
    public void setUpvoteButton() {
        upvoteButton.setOnAction((
                ActionEvent event) -> {
            System.out.println(question.getText());
        });
    }

}
