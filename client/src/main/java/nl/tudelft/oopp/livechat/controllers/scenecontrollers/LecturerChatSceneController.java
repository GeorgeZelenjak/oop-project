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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import nl.tudelft.oopp.livechat.businesslogic.PercentageCalculator;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.businesslogic.QuestionManager;
import nl.tudelft.oopp.livechat.businesslogic.CreateFile;
import nl.tudelft.oopp.livechat.controllers.popupcontrollers.PollingManagementPopupController;
import nl.tudelft.oopp.livechat.data.*;
import nl.tudelft.oopp.livechat.servercommunication.LectureSpeedCommunication;
import nl.tudelft.oopp.livechat.servercommunication.PollCommunication;
import nl.tudelft.oopp.livechat.uielements.QuestionCellLecturer;
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
    private Button copyKey;

    @FXML
    private Button copyId;

    @FXML
    private Button goToUserManualButton;

    @FXML
    private Label showLabel;

    @FXML
    private Pane speedBackground;

    @FXML
    private Text speedText;

    @FXML
    private Button speedButton;

    @FXML
    private Button createPolling;


    @FXML
    private Button popupVoteResults;

    @FXML
    private CheckBox sortByTimeCheckBox;

    @FXML
    private Text voteCountFast;

    @FXML
    private Text voteCountSlow;

    @FXML
    private Line slowerVotesPercentLine;

    @FXML
    private Line fasterVotesPercentLine;

    @FXML
    private Button goToLectureModeButton;

    @FXML
    private Button exportQA;

    @FXML
    private Button closeLectureButton;

    @FXML
    private Button leaveLecture;

    @FXML
    private Label sortByLabel;

    @FXML
    ObservableList<Question> observableList = FXCollections.observableArrayList();

    private static List<Integer> lectureSpeeds;

    private List<Question> questions;

    private Timeline timelineFetch;

    /**
     * Method that runs at scene initalization.
     * @param location location of scene
     * @param resourceBundle resources brought around
     */
    public void initialize(URL location, ResourceBundle resourceBundle) {
        lectureNameText.setText(Lecture.getCurrent().getName());
        userNameText.setText(User.getUserName());

        System.out.println(fasterVotesPercentLine.getEndX());
        slowerVotesPercentLine.setEndX(fasterVotesPercentLine.getEndX());

        timelineFetch = new Timeline(new KeyFrame(
                Duration.millis(1500),
            ae -> {
                fetchQuestions();
                getVotesOnLectureSpeed();
                adjustLectureSpeedLines();
                fetchPoll();
            }));
        timelineFetch.setCycleCount(Animation.INDEFINITE);
        timelineFetch.play();
        setTooltips();
    }


    /**
     * Gets votes on lecture speed.
     */
    public void getVotesOnLectureSpeed() {
        UUID uuid = Lecture.getCurrent().getUuid();
        lectureSpeeds = LectureSpeedCommunication.getVotesOnLectureSpeed(uuid);
        voteCountFast.setText("Too fast: "
                + lectureSpeeds.get(0));
        voteCountSlow.setText("Too slow: "
                + lectureSpeeds.get(1));
    }

    /**
     * Reset lecture speed.
     */
    public void resetLectureSpeed() {
        UUID uuid = Lecture.getCurrent().getUuid();
        UUID modkey = Lecture.getCurrent().getModkey();
        LectureSpeedCommunication.resetLectureSpeed(uuid,modkey);
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
        Question.setCurrentList(list);

        questions = Question.getCurrentList();
        questions = QuestionManager.filter(answeredCheckBox.isSelected(),
                unansweredCheckBox.isSelected(), questions);
        QuestionManager.sort(sortByVotesCheckBox.isSelected(),
                sortByTimeCheckBox.isSelected(), questions);

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
        String myString = Lecture.getCurrent().getUuid().toString();
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
        String myString = Lecture.getCurrent().getModkey().toString();
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
            timelineFetch.stop();
            NavigationController.getCurrent().goBack();
            NavigationController.getCurrent().goBack();
        }
    }

    /**
     * Go to user manual.
     *
     * @throws IOException if something happens
     */
    public void goToUserManual() {

        NavigationController.getCurrent().goToUserManual();
    }


    /**
     * Close lecture.
     *
     * @throws IOException if something happens
     */
    public void closeLecture() {
        Alert alert = AlertController.createAlert(Alert.AlertType.CONFIRMATION,
                "Confirm your action", "Are you sure do you want to close this lecture?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            timelineFetch.stop();
            LectureCommunication.closeLecture(Lecture.getCurrent().getUuid().toString(),
                    Lecture.getCurrent().getModkey().toString());
            NavigationController.getCurrent().goToMainScene();
        }
    }

    /**
     * Lecturer mode.
     */
    public void lecturerMode() {

        this.exportQA.setDisable(!this.exportQA.isDisabled());
        this.exportQA.setVisible(!this.exportQA.isVisible());

        this.sortByLabel.setDisable(!this.sortByLabel.isDisabled());
        this.sortByLabel.setVisible(!this.sortByLabel.isVisible());

        this.goToUserManualButton.setDisable(!this.goToUserManualButton.isDisabled());
        this.goToUserManualButton.setVisible(!this.goToUserManualButton.isVisible());

        this.sortByVotesCheckBox.setDisable(!this.sortByVotesCheckBox.isDisabled());
        this.sortByVotesCheckBox.setVisible(!this.sortByVotesCheckBox.isVisible());

        this.sortByTimeCheckBox.setDisable(!this.sortByTimeCheckBox.isDisabled());
        this.sortByTimeCheckBox.setVisible(!this.sortByTimeCheckBox.isVisible());

        this.unansweredCheckBox.setDisable((!this.unansweredCheckBox.isDisabled()));
        this.unansweredCheckBox.setVisible(!this.unansweredCheckBox.isVisible());

        this.answeredCheckBox.setDisable(!this.answeredCheckBox.isDisabled());
        this.answeredCheckBox.setVisible(!this.answeredCheckBox.isVisible());

        this.speedBackground.setDisable(!this.speedBackground.isDisabled());
        this.speedBackground.setVisible(!this.speedBackground.isVisible());

        this.createPolling.setDisable(!this.createPolling.isDisabled());
        this.createPolling.setVisible(!this.createPolling.isVisible());

        this.popupVoteResults.setDisable(!this.popupVoteResults.isDisabled());
        this.popupVoteResults.setVisible(!this.popupVoteResults.isVisible());

        this.speedText.setDisable(!this.speedText.isDisabled());
        this.speedText.setVisible(!this.speedText.isVisible());

        this.showLabel.setDisable(!this.showLabel.isDisabled());
        this.showLabel.setVisible(!this.showLabel.isVisible());

        this.copyKey.setDisable(!this.copyKey.isDisabled());
        this.copyKey.setVisible(!this.copyKey.isVisible());

        this.copyId.setDisable(!this.copyId.isDisabled());
        this.copyId.setVisible(!this.copyId.isVisible());
    }

    /**
     * Method that exports all Questions and answers of a lecture.
     */
    public void exportQuestionsAndAnswers() {

        String alertText = "Press ok to export all questions and answers to file";
        Alert alert = AlertController.createAlert(
                Alert.AlertType.CONFIRMATION, "Exporting Q&A", alertText);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                CreateFile file = new CreateFile("exportedQuestions/");

                List<Question> list = QuestionCommunication.fetchQuestions();

                if (list == null || list.size() == 0)
                    AlertController.alertError(
                            "Error exporting questions", "There are no questions!");

                file.writeToFile(list);
            }
        });
    }

    private void adjustLectureSpeedLines() {
        if (lectureSpeeds == null) {
            return;
        }
        //Sets the
        slowerVotesPercentLine.setStartX(PercentageCalculator.determineNewStartCoordinates(
                fasterVotesPercentLine.getStartX(), fasterVotesPercentLine.getEndX(),
                lectureSpeeds.get(0), lectureSpeeds.get(1)));

        //Makes it so that if the blue line is just a dot, users do not see it
        slowerVotesPercentLine.setVisible(slowerVotesPercentLine.getEndX()
                != slowerVotesPercentLine.getStartX());
    }


    /**
     * Popup polling management.
     */
    public void popupPollingManagement() {
        if (PollingManagementPopupController.getInEditingPoll() == null) {
            PollingManagementPopupController.setInEditingPoll(new Poll());
            PollingManagementPopupController.setInEditingOptions(new ArrayList<PollOption>());
        }
        NavigationController.getCurrent().popupPollingManagement();
    }

    private void setTooltips() {
        //Tooltips
        copyId.setTooltip(new Tooltip("Copy the lecture's ID to clipboard"));
        copyKey.setTooltip(new Tooltip("Copy the moderator key to clipboard"));

        goToLectureModeButton.setTooltip(new Tooltip("Enable/Disable lecturer mode"));

        goToUserManualButton.setTooltip(new Tooltip("Open Help & Documentation page"));

        speedButton.setTooltip(new Tooltip("Open/Reopen voting on lecture speed"));

        exportQA.setTooltip(new Tooltip("Export this lecture's content"));
        closeLectureButton.setTooltip(new Tooltip("Close this lecture"));

        leaveLecture.setTooltip(new Tooltip("Leave this lecture"));
        createPolling.setTooltip(new Tooltip("Create a polling question"));

        popupVoteResults.setTooltip(new Tooltip("Create a quiz"));
    }

    private void fetchPoll() {

        PollAndOptions fetched = (
                PollCommunication.fetchPollAndOptionsModerator(
                        Lecture.getCurrent().getUuid(),
                        Lecture.getCurrent().getModkey()));
        if (fetched == null) {
            return;
        }
        PollAndOptions.setCurrent(fetched);
    }

    public void popupVoteResults() {
        NavigationController.getCurrent().popupPollResult();
    }


}

