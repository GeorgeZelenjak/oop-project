package nl.tudelft.oopp.livechat.controllers.cellcontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class for Cell data for the lecturer.
 */
public class CellLecturerController implements Initializable {

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

    @FXML
    private Button banButton;

    private Question question;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteButton.setTooltip(new Tooltip("Delete this question"));
        replyButton.setTooltip(new Tooltip("Answer this question"));

        editButton.setTooltip(new Tooltip("Edit this question"));
        isAnsweredButton.setTooltip(new Tooltip("Mark this question as answered"));
    }

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
                        Lecture.getCurrent().getModkey(), null);
                System.out.println(question.getVotes());
            });
        }
    }

    /**
     * Sets ban button to ban a user.
     */
    public void setBanUser() {
        banButton.setOnAction((ActionEvent event) -> {
            if (question.getOwnerName().contains(" (banned)")) {
                boolean ban = AlertController.alertConfirmation("It looks as if the user is"
                                + "already banned",
                        "However, he/she might have just played with their name"
                                + "and are not banned in fact.\n"
                        + "Would you still like to ban?");
                if (!ban) return;
            }
            int[] result = showPopup();
            if (result[2] != 1) {
                return;
            }
            int time = result[0] * 60;
            boolean byIp = result[1] != 0;
            LectureCommunication.ban(Lecture.getCurrent().getModkey().toString(),
                    question.getId(), time, byIp);
        });
    }

    /**
     * Sets edit button to edit the question content.
     */
    public void setEditButton() {
        editButton.setOnAction((ActionEvent e) -> {
            Question.setCurrent(question);
            NavigationController.getCurrent().popupEditQuestion();
        });
    }

    /**
     * Sets delete button to delete any question.
     */
    public void setDeleteQuestion() {
        deleteButton.setOnAction((ActionEvent event) ->
                QuestionCommunication.modDelete(question.getId(),
                    Lecture.getCurrent().getModkey()));
    }

    /**
     * Method that sets the answer text of a question
     *  if the question was answered with a text answer.
     */
    public void setAnswerText(String value) {
        answerText.setText("Answer: " + value);
    }

    public void setStatusText(String value) {
        answerText.setText(value);

    }

    /**
     * Method that controls the reply Button functionality.
     * A popup will appear to enter the answer text.
     */
    public void replyAnswer() {
        replyButton.setOnAction((ActionEvent event) -> {
            Question.setCurrent(question);
            NavigationController.getCurrent().popupAnswerQuestion();
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

    //res[0] is time, res[1] is 0 if by id, 1 if by ip, res[2] if the button was submitted
    private int[] showPopup() {
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton byId = new RadioButton("Ban by user id");
        RadioButton byIp = new RadioButton("Ban by user ip");
        byId.setToggleGroup(toggleGroup);
        byId.setSelected(true);
        byIp.setToggleGroup(toggleGroup);

        Spinner<Integer> time = new Spinner<>();
        time.setEditable(true);
        time.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 200));

        int[] res = new int[3];

        Stage window = new Stage();
        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            res[2] = 1;
            window.close();
        });

        Label labelHeader = new Label("Choose the way you would how you would like to ban");
        Label labelTime = new Label("Choose the number of minutes to ban");

        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(labelHeader, byId, byIp, labelTime, time, submit);

        Scene scene = new Scene(box, 300, 200);

        window.setScene(scene);
        window.showAndWait();

        res[0] = time.getValue() > 200 ? 200 : time.getValue();
        res[1] = byIp.isSelected() ? 1 : 0;
        return res;
    }
}
