package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import nl.tudelft.oopp.livechat.businesslogic.PercentageCalculator;
import nl.tudelft.oopp.livechat.controllers.gui.AlertController;
import nl.tudelft.oopp.livechat.controllers.gui.NavigationController;
import nl.tudelft.oopp.livechat.businesslogic.QuestionManager;
import nl.tudelft.oopp.livechat.businesslogic.CreateFile;
import nl.tudelft.oopp.livechat.controllers.popupcontrollers.PollingManagementPopupController;
import nl.tudelft.oopp.livechat.data.*;
import nl.tudelft.oopp.livechat.servercommunication.LectureSpeedCommunication;
import nl.tudelft.oopp.livechat.servercommunication.PollCommunication;
import nl.tudelft.oopp.livechat.uielements.QuestionCellLecturer;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
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
    private Button frequencyButton;

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
    private Pane hideBottomPane;

    @FXML
    private Pane hideTopPane;

    @FXML
    private Group lecturerGroup;

    @FXML
    ObservableList<Question> observableList = FXCollections.observableArrayList();

    private Thread fetchingThread;

    private static List<Integer> lectureSpeeds;

    private List<Question> questions;

    private Timeline timelineFetch;

    /**
     * Method that runs at scene initialization.
     * @param location location of scene
     * @param resourceBundle resources brought around
     */
    public void initialize(URL location, ResourceBundle resourceBundle) {
        lectureNameText.setText(Lecture.getCurrent().getName());
        userNameText.setText(User.getUserName());

        System.out.println(fasterVotesPercentLine.getEndX());
        slowerVotesPercentLine.setEndX(fasterVotesPercentLine.getEndX());

        getQuestions(true);
        timelineFetch = new Timeline(new KeyFrame(Duration.millis(1000), ae -> {
            setQuestions();
            getVotesOnLectureSpeed();
            adjustLectureSpeedLines();
            fetchPoll();
        }));
        timelineFetch.setCycleCount(Animation.INDEFINITE);
        timelineFetch.play();
        fetchingThread = new Thread(
            () -> {
                while (Lecture.getCurrent() != null) {
                    List<Question> list = QuestionCommunication.fetchQuestions(false);
                    if (list != null) {
                        Question.setCurrentList(list);
                        System.out.println("i'm alive");
                    }
                }
            }
                );
        fetchingThread.setDaemon(true);
        fetchingThread.start();
        setTooltips();
    }


    /**
     * Gets votes on lecture speed.
     */
    public void getVotesOnLectureSpeed() {
        UUID uuid = Lecture.getCurrent().getUuid();
        lectureSpeeds = LectureSpeedCommunication.getVotesOnLectureSpeed(uuid);
        if (lectureSpeeds == null) return;
        voteCountFast.setText("Too fast: " + lectureSpeeds.get(0));
        voteCountSlow.setText("Too slow: " + lectureSpeeds.get(1));
    }

    /**
     * Resets lecture speed.
     */
    public void resetLectureSpeed() {
        UUID uuid = Lecture.getCurrent().getUuid();
        UUID modkey = Lecture.getCurrent().getModkey();
        LectureSpeedCommunication.resetLectureSpeed(uuid,modkey);
    }

    private void getQuestions(boolean firstTime) {
        List<Question> list = QuestionCommunication.fetchQuestions(firstTime);
        if (list == null) {
            return;
        }
        if (list.size() == 0) {
            System.out.println("There are no questions");
        }
    }

    /**
     * Fetches questions.
     */
    public void setQuestions() {
        questions = Question.getCurrentList();
        questions = QuestionManager.filter(answeredCheckBox.isSelected(),
                unansweredCheckBox.isSelected(), questions);
        QuestionManager.sort(sortByVotesCheckBox.isSelected(),
                sortByTimeCheckBox.isSelected(), questions);

        displayQuestions();
    }

    /**
     * Displays the questions.
     */
    private void displayQuestions() {
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
     * Set the frequency of asking questions.
     */
    public void setFrequency() {
        Lecture lecture = LectureCommunication
                .joinLectureById(Lecture.getCurrent().getUuid().toString());
        if (lecture == null) return;
        Lecture.getCurrent().setFrequency(lecture.getFrequency());

        int[] result = showPopup();
        if (result[1] != 1) {
            return;
        }
        int time = result[0];
        LectureCommunication.setFrequency(Lecture.getCurrent().getUuid().toString(),
                Lecture.getCurrent().getModkey().toString(), time);
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
            fetchingThread.stop();
            fetchingThread = null;
            Question.setCurrentList(new ArrayList<>());
            NavigationController.getCurrent().goBack();
            NavigationController.getCurrent().goBack();
            Lecture.setCurrent(null);
        }
    }

    /**
     * A helper method for a popup to select the frequency of asking questions.
     * @return res[0] is the selected frequency, res[1] if the button was submitted
     */
    private int[] showPopup() {
        Spinner<Integer> frequency = new Spinner<>(0, 300, Lecture.getCurrent().getFrequency());
        frequency.setInitialDelay(new Duration(0));

        int[] result = new int[2];
        Stage window = new Stage();
        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            result[1] = 1;
            window.close();
        });

        Label label = new Label("Choose the frequency of asking questions in seconds (max 300)");

        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(label, frequency, submit);

        Scene scene = new Scene(box, 350, 150);
        window.setScene(scene);
        window.showAndWait();

        result[0] = frequency.getValue() > 300 ? 300 : frequency.getValue();
        return result;
    }

    /**
     * Go to user manual.
     */
    public void goToUserManual() {
        NavigationController.getCurrent().goToUserManual();
    }


    /**
     * Close lecture.
     */
    public void closeLecture() {
        Alert alert = AlertController.createAlert(Alert.AlertType.CONFIRMATION,
                "Confirm your action", "Are you sure do you want to close this lecture?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            timelineFetch.stop();
            fetchingThread.stop();
            fetchingThread = null;
            Question.setCurrentList(new ArrayList<>());
            LectureCommunication.closeLecture(Lecture.getCurrent().getUuid().toString(),
                    Lecture.getCurrent().getModkey().toString());
            NavigationController.getCurrent().goToMainScene();
            Lecture.setCurrent(null);
        }
    }

    /**
     * Lecturer mode.
     */
    public void lecturerMode() {
        this.hideBottomPane.setDisable(!this.hideBottomPane.isDisabled());
        this.hideBottomPane.setVisible(!this.hideBottomPane.isVisible());

        this.hideTopPane.setDisable(!this.hideTopPane.isDisabled());
        this.hideTopPane.setVisible(!this.hideTopPane.isVisible());

        this.lecturerGroup.setDisable(!this.lecturerGroup.isDisabled());
        this.lecturerGroup.setVisible(!this.lecturerGroup.isVisible());

        updateQuestionsLecturerMode();
        if (goToLectureModeButton.getText().equals("Lecturer Mode")) {
            goToLectureModeButton.setText("Quit Lecturer Mode");
        } else goToLectureModeButton.setText("Lecturer Mode");
    }

    /**
     * A helper method to show only relevant questions in the lecturer mode.
     */
    private void updateQuestionsLecturerMode() {
        if (!this.lecturerGroup.isVisible()) {
            answeredCheckBox.setSelected(false);
            unansweredCheckBox.setSelected(true);
            sortByTimeCheckBox.setSelected(false);
            sortByVotesCheckBox.setSelected(true);
            questions = QuestionManager.filter(false, true, Question.getCurrentList());
            QuestionManager.sort(true, false, questions);
        } else {
            answeredCheckBox.setSelected(true);
            unansweredCheckBox.setSelected(true);
            sortByTimeCheckBox.setSelected(true);
            sortByVotesCheckBox.setSelected(false);
            questions = QuestionManager.filter(false, true, Question.getCurrentList());
            QuestionManager.sort(false, true, questions);
        }
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
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(
                        showLabel.getScene().getWindow());
                if (selectedDirectory == null) return;
                CreateFile file = new CreateFile();

                if (!file.setPath(selectedDirectory.getAbsolutePath())) {
                    AlertController.alertError(
                            "Invalid path name", "Invalid path name!");
                    return;
                }
                if (!file.createFile()) {
                    AlertController.alertError(
                            "Error when creating file", "Error when creating file!");
                    return;
                }

                List<Question> list = QuestionCommunication.fetchQuestions(true);

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
        slowerVotesPercentLine.setStartX(PercentageCalculator.determineNewStartCoordinates(
                fasterVotesPercentLine.getStartX(), fasterVotesPercentLine.getEndX(),
                lectureSpeeds.get(0), lectureSpeeds.get(1)));

        //Makes it so that if the blue line is just a dot, users do not see it
        slowerVotesPercentLine.setVisible(slowerVotesPercentLine.getEndX()
                != slowerVotesPercentLine.getStartX());
        displayQuestions();
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

