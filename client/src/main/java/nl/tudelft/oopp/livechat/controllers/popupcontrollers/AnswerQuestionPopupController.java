package nl.tudelft.oopp.livechat.controllers.popupcontrollers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * The type Answer question popup controller.
 */
public class AnswerQuestionPopupController implements Initializable {

    @FXML
    private TextArea answerTextArea;

    @FXML
    private Button submitAnswerButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Text questionText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionText.setText(Question.getCurrentQ().getText());
    }


    /**
     * Submit the answer.
     */
    public void submitAnswer() {
        QuestionCommunication.markedAsAnswered(Question.getCurrentQ().getId(),
                Lecture.getCurrent().getModkey(), answerTextArea.getText());
        closeTheScene();
    }

    /**
     * Close the scene.
     */
    public void closeTheScene() {
        QuestionCommunication.setStatus(Question.getCurrentQ().getId(),
                Lecture.getCurrent().getModkey(), "new", User.getUid());
        Stage stage = (Stage) answerTextArea.getScene().getWindow();
        stage.close();
    }

}
