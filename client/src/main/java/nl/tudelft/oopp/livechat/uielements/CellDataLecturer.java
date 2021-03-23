package nl.tudelft.oopp.livechat.uielements;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.stage.Stage;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
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

    @FXML
    private Button banButton;


    private Question question;


    /**
     * Creates a new Cell data object.
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

    public void setBanUser() {
        banButton.setOnAction((ActionEvent event) -> {
            int[] result = showPopup();
            int time = result[0] * 60;
            boolean byIp = result[1] != 0;
            LectureCommunication.ban(Lecture.getCurrentLecture().getModkey().toString(),
                    question.getOwnerId(), time, byIp);
        });
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

    //res[0] is time, res[1] is 0 if by id, 1 if by ip
    private int[] showPopup() {
        int[] res = new int[2];

        Stage window = new Stage();
        VBox box = new VBox();

        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton byId = new RadioButton("Ban by user id");
        RadioButton byIp = new RadioButton("Ban by user ip");
        byId.setToggleGroup(toggleGroup);
        byId.setSelected(true);
        byIp.setToggleGroup(toggleGroup);

        //Button byId = new Button("Ban by user id");
        //Button byIp = new Button("Ban by ip");

        //byId.setOnAction(e -> res[1] =  0);
        //byIp.setOnAction(e -> res[1] =  1);

        Label labelHeader = new Label("Choose the way you would how you would like to ban");
        Label labelTime = new Label("Choose the number of minutes to ban");
        Spinner<Integer> time = new Spinner<>();
        time.setEditable(true);
        time.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200));
        //time.valueProperty().addListener((v, oldValue, newValue) -> res[0] = newValue);

        Button submit = new Button("Submit");
        submit.setOnAction(e -> window.close());

        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(labelHeader, byId, byIp, labelTime, time, submit);

        Scene scene = new Scene(box, 300, 200);

        window.setScene(scene);
        window.showAndWait();

        res[0] = time.getValue();
        res[1] = byIp.isSelected() ? 1 : 0;
        return res;
    }
}
