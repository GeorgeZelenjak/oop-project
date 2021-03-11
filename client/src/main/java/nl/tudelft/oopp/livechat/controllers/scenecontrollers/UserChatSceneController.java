package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * The type User chat page controller.
 */
public class UserChatSceneController implements Initializable {



    @FXML
    private TextField inputQuestion;
    @FXML
    private ListView<String> questionPane;
    @FXML
    private Text lectureName;

    /**
     * method that runs when the scene is first initialized.
     * @param location location of current scene
     * @param resourceBundle resource bundle
     */

    public void initialize(URL location, ResourceBundle resourceBundle) {
        lectureName.setText(Lecture.getCurrentLecture().getName());
        lectureName.setTextAlignment(TextAlignment.CENTER);

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(2500),
            ae -> fetchQuestions()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Fetch questions.
     */
    public void fetchQuestions() {

        List<Question> list = QuestionCommunication.fetchQuestions();
        if (list == null)
            return;
        List<String> listString = list.stream()
                .map(Question::getText).collect(Collectors.toList());
        questionPane.getItems().clear();
        questionPane.getItems().addAll(listString);
    }

    /**
     * Go back to main.
     *
     * @throws IOException the io exception
     */
    public void goBackToMain() throws IOException {

        //Navigating back to Main Page

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm your action");
        alert.setHeaderText(null);

        alert.setContentText("Are you sure do you want to quit this lecture?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NavigationController.getCurrentController().goToMainScene();
        }
    }


    /**
     * Go to user manual.
     *
     * @throws IOException the io exception
     */
    public void goToUserManual() throws IOException {
        NavigationController.getCurrentController().goToUserManual();

    }

    /**
     * Go to settings.
     *
     * @throws IOException the io exception
     */
    public void goToSettings() throws IOException {
        NavigationController.getCurrentController().goToSettings();
    }

    /**
     * Send a question to the server.
     *
     * @param ae the enter button
     * @return Integer showing status of the action
     *      1- Everything is good
     *      -1 -Lecture has not been initialized
     *      -2/ -3 -Server error.
     */
    @FXML
    public int askQuestion(ActionEvent ae) {

        int ret = QuestionCommunication.askQuestion(inputQuestion.getText());
        //inputQuestion.setText("");



        System.out.println(ret);
        if (ret <= 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);

            alert.setContentText("There was a problem with asking question!");
            alert.showAndWait();
        }

        Question question = new Question(
                Lecture.getCurrentLecture().getUuid(), inputQuestion.getText(), 0);


        questionPane.getItems().add(question.getText());

        return (ret);

    }

}
