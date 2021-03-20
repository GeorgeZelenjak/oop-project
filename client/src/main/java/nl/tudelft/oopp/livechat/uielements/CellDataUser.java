package nl.tudelft.oopp.livechat.uielements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

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

    @FXML
    private Label answeredTick;

    @FXML
    private Button deleteButton;

    @FXML
    private Text answerText;

    private Question question;


    /**
     * Creates a new Cell data object.
     */
    public CellDataUser() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/questionCellUser.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the question content.
     * @param content the question content
     */
    public void setContent(String content) {
        questionText.setText(content);
    }

    /**
     * Makes a visible tick appear for answered questions.
     */
    public void markAnswered() {
        if (question.isAnswered()) {
            answeredTick.setVisible(true);
        }
    }

    /**
     * Sets the owner name of the question.
     * @param owner the owner of the question
     */
    public void setOwnerName(String owner) {
        questionOwner.setText(owner);
    }

    /**
     * Gets the anchor pane the info is in.
     * @return the anchor pane
     */
    public AnchorPane getBox() {
        return questionBoxAnchorPane;
    }

    /**
     * Sets the question.
     * @param question the question
     */
    public void setQuestion(Question question) {
        this.question = question;
    }

    /**
     * Sets the number of upvotes for the question.
     * @param number the number of upvotes for the question
     */
    public void setNumberOfUpvotes(int number) {
        numberOfUpvotes.setText(String.valueOf(number));
    }

    /**
     * Sets the time the question was asked.
     * @param timestamp the time the question was asked
     */
    public void setTimestamp(Timestamp timestamp) {
        dateStamp.setText(timestamp.toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Sets upvote button to upvote/unvote the question.
     */
    public void setUpvoteButton() {
        upvoteButton.setOnAction((ActionEvent event) -> {
            QuestionCommunication.upvoteQuestion(question.getId(), User.getUid());
            System.out.println(question.getVotes());
        });
    }

    /**
     * Sets delete button if the question was asked by the current user.
     */
    public void setDeleteButton() {
        if (User.getAskedQuestionIds().contains(question.getId())) {
            deleteButton.setDisable(false);
            deleteButton.setVisible(true);
            deleteButton.setOnAction((ActionEvent event) ->
                    QuestionCommunication.deleteQuestion(question.getId(), User.getUid()));
        }
    }

    /**
     * Method that sets the answer text of a question
     *  if the question was answered with a text answer.
     */
    public void setAnswerText() {
        if (question.isAnswered()) {
            if (question.getAnswerText() != null && !question.getAnswerText().equals(" "))
               answerText.setText("Answer: " + question.getAnswerText());
        }
    }
}
