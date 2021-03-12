package nl.tudelft.oopp.livechat.data;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.io.IOException;
import java.sql.Timestamp;

public class CellDataUser {

    @FXML
    private Text questionText;

    @FXML
    private AnchorPane questionBoxAnchorPane;

    @FXML
    private Button upvoteButton;

    @FXML
    private Text questionOwner;

    @FXML
    private Text numberOfUpvotes;

    @FXML
    private Text dateStamp;

    private Question question;


    /**
     * Instantiates a new Cell data.
     */
    public CellDataUser() {
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

    public void setOwnerName(String owner) {
        questionOwner.setText(owner);
    }

    public AnchorPane getBox() {
        return questionBoxAnchorPane;
    }

    public void setQuestion(Question question) {

        this.question = question;
    }

    public void setNumberOfUpvotes(int number) {
        numberOfUpvotes.setText(String.valueOf(number));
    }

    public void setTimestamp(Timestamp timestamp) {

        dateStamp.setText(timestamp.toLocalDateTime().toString());
    }

    /**
     * Sets upvote button.
     */
    public void setUpvoteButton() {
        upvoteButton.setOnAction((
                ActionEvent event) -> {
            QuestionCommunication.upvoteQuestion(question.getId(), User.getUid());

            System.out.println(question.getVotes());
        });
    }
}
