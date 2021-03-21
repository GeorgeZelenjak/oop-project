package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.businesslogic.QuestionManager;
import nl.tudelft.oopp.livechat.data.Lecture;

import nl.tudelft.oopp.livechat.data.Question;

import nl.tudelft.oopp.livechat.servercommunication.LectureSpeedCommunication;
import nl.tudelft.oopp.livechat.uielements.QuestionCellUser;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
 * Class for the UserChat Scene controller.
 */
public class UserChatSceneController implements Initializable {

    @FXML
    private TextArea questionInputTextArea;

    @FXML
    private ListView<Question> questionPaneListView;

    @FXML
    private Text lectureNameText;

    @FXML
    private Text userNameText;

    @FXML
    private CheckBox sortByVotesCheckBox;

    @FXML
    private CheckBox sortByTimeCheckBox;

    @FXML
    private CheckBox answeredCheckBox;

    @FXML
    private CheckBox unansweredCheckBox;

    @FXML
    private CheckBox voteOnLectureSpeedFast;

    @FXML
    private CheckBox voteOnLectureSpeedSlow;

    /**
     * The Observable list.
     */
    @FXML
    ObservableList<Question> observableList = FXCollections.observableArrayList();

    private List<Question> questions;

    /**
     * Method that runs when the scene is first initialized.
     * @param location location of current scene
     * @param resourceBundle resource bundle
     */
    public void initialize(URL location, ResourceBundle resourceBundle) {
        lectureNameText.setText(Lecture.getCurrentLecture().getName());
        userNameText.setText(User.getUserName());

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> {
            fetchQuestions();
            getVotesOnLectureSpeed();
        }));
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
        Question.setCurrentQuestions(list);

        questions = Question.getCurrentQuestions();
        questions = QuestionManager.filter(answeredCheckBox.isSelected(),
                unansweredCheckBox.isSelected(), questions);
        QuestionManager.sort(sortByVotesCheckBox.isSelected(),
                sortByTimeCheckBox.isSelected(), questions);
        System.out.println("sorted");

        observableList.setAll(questions);
        questionPaneListView.setItems(observableList);

        questionPaneListView.setCellFactory(
                new Callback<ListView<Question>, ListCell<Question>>() {
                    @Override
            public ListCell<Question> call(ListView<Question> listView) {
                        return new QuestionCellUser();
                    }
                });
        //System.out.println(list.size());

        questionPaneListView.getItems().clear();
        questionPaneListView.getItems().addAll(questions);
    }

    /**
     * Go back to main.
     * @throws IOException the io exception
     */
    public void goBackToMain() throws IOException {

        //Navigating back to Main Page

        Alert alert = AlertController.createAlert(Alert.AlertType.CONFIRMATION,
                "Confirm your action", "Are you sure do you want to quit this lecture?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NavigationController.getCurrentController().goToMainScene();
        }
    }

    /**
     * Go to user manual.
     * @throws IOException the io exception
     */
    public void goToUserManual() throws IOException {
        NavigationController.getCurrentController().goToUserManual();
    }

    /**
     * Go to settings.
     * @throws IOException the io exception
     */
    public void goToSettings() throws IOException {
        NavigationController.getCurrentController().goToSettings();
    }

    /**
     * Send a question to the server.
     * @return Integer showing status of the action
     *      1- Everything is good
     *      -1 -Lecture has not been initialized
     *      -2/ -3 -Server error.
     *      -4 - too long question
     *      -5 empty field
     */
    public int askQuestion() {
        String text = questionInputTextArea.getText();
        if (text.length() == 0) {
            return -5;
        }
        if (text.length() > 2000) {
            AlertController.alertWarning("Long question",
                    "Your question is too long! (max 2000 characters)");
            return -4;
        }
        int ret = QuestionCommunication.askQuestion(text);
        //inputQuestion.setText("");

        System.out.println(ret);
        if (ret < 0) {
            AlertController.alertError("ERROR",
                    "There was a problem with asking question!");
        }

        Question question = new Question(
                Lecture.getCurrentLecture().getUuid(), questionInputTextArea.getText(), 0);

        //questionPaneListView.getItems().add(question.getText());

        questionInputTextArea.clear();

        //TODO this will be removed when we implement a more efficient polling
        fetchQuestions();
        return (ret);
    }

    /**
     * Vote on lecture speed fast.
     *
     * @return 0 if everthing is fine -1 if not
     */
    public int voteOnLectureSpeedFast() {
        voteOnLectureSpeedSlow.setSelected(false);

        return LectureSpeedCommunication.voteOnLectureSpeed(
                User.getUid(),
                Lecture.getCurrentLecture().getUuid(),
                "faster");
    }

    /**
     * Vote on lecture speed slow.
     *
     * @return 0 if everthing is fine -1 if not
     */
    public int voteOnLectureSpeedSlow() {
        voteOnLectureSpeedFast.setSelected(false);

        return LectureSpeedCommunication.voteOnLectureSpeed(
                User.getUid(),
                Lecture.getCurrentLecture().getUuid(),
                "slower");
    }

    /**
     * Gets votes on lecture speed.
     */
    public void getVotesOnLectureSpeed() {
        UUID uuid = Lecture.getCurrentLecture().getUuid();
        List<Integer> speeds = LectureSpeedCommunication.getVotesOnLectureSpeed(uuid);
        if (speeds != null && speeds.get(0).equals(0) && speeds.get(1).equals(0)) {
            voteOnLectureSpeedFast.setSelected(false);
            voteOnLectureSpeedSlow.setSelected(false);
        }
    }

}
