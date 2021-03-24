package nl.tudelft.oopp.livechat.controllers.cellcontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

/**
 * Class for Cell data for the lecturer.
 */
public class CellLecturerController {

    @FXML
    private Text questionText;

    @FXML
    private Button isAnsweredButton;

    @FXML
    private AnchorPane questionBoxAnchorPane;

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

    @FXML
    private Button replyButton;

    @FXML
    private Button editButton;

    private Question question;


    /**
     * Creates a new Cell data object.
     */
    public CellLecturerController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/cell/questionCellLecturer.fxml"));
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
     * Sets mark as answered button to mark the question as answered without a text-based reply.
     */
    public void setAnsweredQuestion() {
        if (question.getAnswerText() == null || question.getAnswerText().equals(" ")) {
            isAnsweredButton.setOnAction((ActionEvent event) -> {
                QuestionCommunication.markedAsAnswered(question.getId(),
                        Lecture.getCurrentLecture().getModkey(), null);
                System.out.println(question.getVotes());
            });
        }
    }

    /**
     * Sets edit button to edit the question content.
     */
    public void setEditButton() {
        editButton.setOnAction((ActionEvent e) -> {
            Question.setCurrentQuestion(question);
            NavigationController.getCurrentController().popupEditQuestion();
        });
    }

    /**
     * Sets delete button to delete any question.
     */
    public void setDeleteQuestion() {
        deleteButton.setOnAction((ActionEvent event) ->
                QuestionCommunication.modDelete(question.getId(),
                    Lecture.getCurrentLecture().getModkey()));
    }

    /**
     * Method that sets the answer text of a question
     *  if the question was answered with a text answer.
     */
    public void setAnswerText(String value) {
        answerText.setText("Answer: " + value);
    }

    /**
     * Method that controls the reply Button functionality.
     * A popup will appear to enter the answer text.
     */
    public void replyAnswer() {
        replyButton.setOnAction((ActionEvent event) -> {
            Question.setCurrentQuestion(question);
            NavigationController.getCurrentController().popupAnswerQuestion();
        });

    }

    /**
     * Method that disables the marked as answered button
     *  when the question has already been answered.
     */
    public void disableMarkedAsAnswered() {
        if (question.isAnswered()) {
            isAnsweredButton.setDisable(true);
            isAnsweredButton.setVisible(false);
        }

    }
}
