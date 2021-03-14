package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.businesslogic.QuestionManager;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.uielements.QuestionCellLecturer;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Class for the LectureChat Scene controller.
 */
public class LecturerChatSceneController implements Initializable {

    @FXML
    private Text lectureNameText;

    @FXML
    private Text userNameText;

    @FXML
    private ListView<Question> questionPaneListView;

    @FXML
    private CheckBox sortByVotesCheckBox;

    @FXML
    private CheckBox answeredCheckBox;

    @FXML
    private CheckBox unansweredCheckBox;

    @FXML
    private Button participants;

    @FXML
    private Button copyKey;

    @FXML
    private Button copyId;

    @FXML
    private Button goToUserManualButton;

    @FXML
    private Label showText;
    @FXML
    private Pane pollingBackground;
    @FXML
    private Pane speedBackground;
    @FXML
    private Text pollingText;
    @FXML
    private Text speedText;
    @FXML
    private Button pollingButton;
    @FXML
    private Button speedButton;
    @FXML
    private Button lectureLog;
    @FXML
    private Button viewAnswered;
    @FXML
    private Button reopenPolling;
    @FXML
    private Button goToSettingsButton;
    @FXML
    private Button createPolling;
    @FXML
    private Button createQuiz;

    /**
     * The Observable list.
     */
    @FXML
    ObservableList<Question> observableList = FXCollections.observableArrayList();

    private List<Question> questions;

    /**
     * Method that runs at scene initalization.
     * @param location location of scene
     * @param resourceBundle resources brought around
     */
    public void initialize(URL location, ResourceBundle resourceBundle) {
        lectureNameText.setText(Lecture.getCurrentLecture().getName());
        userNameText.setText(User.getUserName());
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1500),
            ae -> fetchQuestions()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Fetch questions.
     */
    public void fetchQuestions() {
        List<Question> list = QuestionCommunication.fetchQuestions();
        if (list == null) {
            return;
        }
        if (list.size() == 0) {
            System.out.println("There are no questions");
        }
        Question.setCurrentQuestions(list);

        questions = Question.getCurrentQuestions();
        questions = QuestionManager.filter(answeredCheckBox.isSelected(),
                unansweredCheckBox.isSelected(), questions);
        QuestionManager.sort(sortByVotesCheckBox.isSelected(), questions);

        observableList.setAll(questions);
        questionPaneListView.setItems(observableList);

        questionPaneListView.setCellFactory(
                new Callback<ListView<Question>, ListCell<Question>>() {
                    @Override
                    public ListCell<Question> call(ListView<Question> listView) {
                        return new QuestionCellLecturer();
                    }
                });
        questionPaneListView.getItems().clear();
        questionPaneListView.getItems().addAll(questions);
    }

    /**
     * Copy lecture id to clipboard.
     */
    public void copyLectureId() {
        String myString = Lecture.getCurrentLecture().getUuid().toString();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        AlertController.alertInformation("Copied successfully!",
                "Lecture id copied to clipboard");
    }

    /**
     * Copy moderator key to clipboard.
     */
    public void copyModKey() {
        String myString = Lecture.getCurrentLecture().getModkey().toString();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        AlertController.alertInformation("Copied successfully!",
                "Moderator key copied to clipboard");
    }

    /**
     * Go back to main page.
     */
    public void goBackToMain() {
        Alert alert = AlertController.createAlert(Alert.AlertType.CONFIRMATION,
                "Confirm your action", "Are you sure do you want to quit this lecture?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NavigationController.getCurrentController().goBack();
            NavigationController.getCurrentController().goBack();
        }
    }

    /**
     * Go to user manual.
     *
     * @throws IOException if something happens
     */
    public void goToUserManual() throws IOException {

        NavigationController.getCurrentController().goToUserManual();
    }

    /**
     * Go to settings.
     *
     * @throws IOException if something happens
     */
    public void goToSettings() throws IOException {
        NavigationController.getCurrentController().goToSettings();
    }

    /**
     * Close lecture.
     *
     * @throws IOException if something happens
     */
    public void closeLecture() throws IOException {
        LectureCommunication.closeLecture(Lecture.getCurrentLecture().getUuid().toString(),
                Lecture.getCurrentLecture().getModkey().toString());
        NavigationController.getCurrentController().goToMainScene();
    }

    /**
     * Lecturer mode.
     */
    public void lecturerMode() {
        this.answeredCheckBox.setVisible(!this.answeredCheckBox.isVisible());
        this.answeredCheckBox.setDisable(!this.answeredCheckBox.isDisabled());
        this.unansweredCheckBox.setVisible((!this.unansweredCheckBox.isVisible()));
        this.unansweredCheckBox.setDisable((!this.unansweredCheckBox.isDisabled()));
        this.participants.setVisible(!this.participants.isVisible());
        this.participants.setDisable(!this.participants.isDisabled());
        this.copyKey.setVisible(!this.copyKey.isVisible());
        this.copyKey.setDisable(!this.copyKey.isDisabled());
        this.copyId.setVisible(!this.copyId.isVisible());
        this.copyId.setDisable(!this.copyId.isDisabled());
        this.goToUserManualButton.setVisible(!this.goToUserManualButton.isVisible());
        this.goToUserManualButton.setDisable(!this.goToUserManualButton.isDisabled());
        this.showText.setVisible(!this.showText.isVisible());
        this.showText.setDisable(!this.showText.isDisabled());
        this.pollingBackground.setVisible(!this.pollingBackground.isVisible());
        this.pollingBackground.setDisable(!this.pollingBackground.isDisabled());
        this.speedBackground.setVisible(!this.speedBackground.isVisible());
        this.speedBackground.setDisable(!this.speedBackground.isDisabled());
        this.pollingText.setVisible(!this.pollingText.isVisible());
        this.pollingText.setDisable(!this.pollingText.isDisabled());
        this.speedText.setVisible(!this.speedText.isVisible());
        this.speedText.setDisable(!this.speedText.isDisabled());
        this.pollingButton.setVisible(!this.pollingButton.isVisible());
        this.pollingButton.setDisable(!this.pollingButton.isDisabled());
        this.speedButton.setVisible(!this.speedButton.isVisible());
        this.speedButton.setDisable(!this.speedButton.isDisabled());
        this.lectureLog.setVisible(!this.lectureLog.isVisible());
        this.lectureLog.setDisable(!this.lectureLog.isDisabled());
        this.viewAnswered.setVisible(!this.viewAnswered.isVisible());
        this.viewAnswered.setDisable(!this.viewAnswered.isDisabled());
        this.reopenPolling.setVisible(!this.reopenPolling.isVisible());
        this.reopenPolling.setDisable(!this.reopenPolling.isDisabled());
        this.goToSettingsButton.setVisible(!this.goToSettingsButton.isVisible());
        this.goToSettingsButton.setDisable(!this.goToSettingsButton.isDisabled());
        this.createPolling.setVisible(!this.createPolling.isVisible());
        this.createPolling.setDisable(!this.createPolling.isDisabled());
        this.createQuiz.setVisible(!this.createQuiz.isVisible());
        this.createQuiz.setDisable(!this.createQuiz.isDisabled());
    }
}
