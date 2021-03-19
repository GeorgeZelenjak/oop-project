package nl.tudelft.oopp.livechat.uielements;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

/**
 * Class for Cell data for the lecturer.
 */
public class CellDataLecturer {

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
     * Instantiates a new Cell data.
     */
    public CellDataLecturer() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/questionCellLecturer.fxml"));
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

    /**
     * Makes a visible tick appear for answered questions.
     */
    public void markAnswered() {
        if (question.isAnswered()) {
            answeredTick.setVisible(true);
        }
    }

    /**
     * Sets owner name.
     *
     * @param owner the owner
     */
    public void setOwnerName(String owner) {
        questionOwner.setText(owner);
    }

    /**
     * Gets box.
     *
     * @return the box
     */
    public AnchorPane getBox() {
        return questionBoxAnchorPane;
    }

    /**
     * Sets question.
     *
     * @param question the question
     */
    public void setQuestion(Question question) {
        this.question = question;
    }

    /**
     * Sets number of upvotes.
     *
     * @param number the number
     */
    public void setNumberOfUpvotes(int number) {
        numberOfUpvotes.setText(String.valueOf(number));
    }

    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    //TODO why 2 times?
    public void setTimestamp(Timestamp timestamp) {
        dateStamp.setText(timestamp.toLocalDateTime().toString());
        dateStamp.setText(timestamp.toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Sets mark as answered button.
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
     * Sets delete button.
     */
    public void setDeleteQuestion() {
        deleteButton.setOnAction((ActionEvent event) ->
                QuestionCommunication.modDelete(question.getId(),
                    Lecture.getCurrentLecture().getModkey()));
    }

    public void setAnswerText(String value) {
        answerText.setText("Answer: " + value);
    }

    /** Method that controls the reply Button functionality.
     * A popup will appear to enter the answer text.
     *
     */
    public void replyAnswer() {
        replyButton.setOnAction((ActionEvent event) -> {
            TextInputDialog td = new TextInputDialog();

            //We found no workaround for making the td wider (it works so it's not stupid)
            td.setHeaderText("\t\t\tType your Answer in the box below:\t\t\t\t");
            td.setTitle("Enter your answer!");
            td.setHeight(300);


            Optional<String> text = td.showAndWait();


            text.ifPresent(s -> QuestionCommunication.markedAsAnswered(question.getId(),
                    Lecture.getCurrentLecture().getModkey(), s));


        });

    }

    /** Method that disables the marked as answered button.
     *
     */
    public void disableMarkedAsAnswered() {
        if (question.isAnswered()) {
            isAnsweredButton.setDisable(true);
            isAnsweredButton.setVisible(false);
        }

    }
}
