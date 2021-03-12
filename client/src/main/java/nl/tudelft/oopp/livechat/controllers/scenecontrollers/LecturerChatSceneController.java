package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.util.Duration;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.QuestionCellUser;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class LecturerChatSceneController implements Initializable {

    @FXML
    private Text lectureNameLecturer;
    @FXML
    private ListView<Question> questionPaneListView;
    @FXML
    ObservableList<Question> observableList = FXCollections.observableArrayList();

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
     * Close lecture.
     * @throws IOException the io exception
     */
    public void closeLecture() throws IOException {
        LectureCommunication.closeLecture(Lecture.getCurrentLecture().getUuid().toString(),
                Lecture.getCurrentLecture().getModkey().toString());
        NavigationController.getCurrentController().goToMainScene();
    }

    public void initialize(URL location, ResourceBundle resourceBundle) {
        //lectureNameLecturer.setText(Lecture.getCurrentLecture().getName());
       // lectureNameLecturer.setTextAlignment(TextAlignment.CENTER);

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(2500),
                ae -> fetchQuestions()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void fetchQuestions() {

        List<Question> list = QuestionCommunication.fetchQuestions();
        if (list == null || list.size() == 0)
            return;

        observableList.setAll(list);
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
        questionPaneListView.getItems().addAll(list);
    }
}
