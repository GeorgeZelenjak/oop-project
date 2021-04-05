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
 * The type Edit question popup controller.
 */
public class EditQuestionPopupController implements Initializable {
    @FXML
    private TextArea editQuestionTextArea;

    @FXML
    private Button submitEditedQuestionButton;

    @FXML
    private Text questionText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionText.setText(Question.getCurrent().getText());
        editQuestionTextArea.setText(Question.getCurrent().getText());
    }


    /**
     * Submit the edited question.
     */
    public void submitEditedQuestion() {
        QuestionCommunication.edit(Question.getCurrent().getId(),
                Lecture.getCurrent().getModkey(), editQuestionTextArea.getText());
        closeTheScene();
    }

    /**
     * Close the scene.
     */
    public void closeTheScene() {
        QuestionCommunication.setStatus(Question.getCurrent().getId(),
                Lecture.getCurrent().getModkey(), "new", User.getUid());
        Stage stage = (Stage) submitEditedQuestionButton.getScene().getWindow();
        stage.close();
    }
}
