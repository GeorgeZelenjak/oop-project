package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;

import java.io.IOException;
import java.util.UUID;

public class JoinLectureSceneController {

    @FXML
    private TextField enterNameTextField;

    @FXML
    private TextField enterLectureCodeTextField;

    @FXML
    private TextField modkeyTextField;


    /**
     * Toggles the visibility of the modKeyTextField.
     */
    public void onCheckBoxAction() {
        modkeyTextField.setVisible(!modkeyTextField.isVisible());
    }

    /**
     * Go to the lecture if successful.
     * @throws IOException exception when something goes wrong
     */
    public void goToLecture() throws IOException {
        if (enterNameTextField.getText().equals("")) {
            alert(Alert.AlertType.WARNING, "No name entered", "Please enter the name!");
            return;
        } else if (enterLectureCodeTextField.getText().equals("")) {
            alert(Alert.AlertType.WARNING, "No lecture id entered", "Please enter the lecture id!");
            return;
        }

        Lecture.setCurrentLecture(
                LectureCommunication.joinLectureById(enterLectureCodeTextField.getText()));
        Lecture currentLecture = Lecture.getCurrentLecture();

        if (currentLecture == null) {
            alert(Alert.AlertType.ERROR, "Error", "Lecture was not found.");

        } else if (!modkeyTextField.getText().equals("")) {
            joinAsModerator();
        } else {
            joinAsStudent();
        }
    }

    /**
     * Go to lecture by pressing the button.
     *
     * @throws IOException the io exception
     */
    public void goToLectureButton() throws IOException {
        goToLecture();
    }

    /**
     * Go to lecture by pressing enter.
     *
     * @throws IOException the io exception
     */
    public void goToLectureEnter() throws IOException {
        goToLecture();
    }

    /**
     * Join lecture as a student.
     * @throws IOException exception if something goes wrong
     */
    private void joinAsStudent() throws IOException {
        if (!Lecture.getCurrentLecture().isOpen()) {
            alert(Alert.AlertType.INFORMATION,
                    "Lecture not open yet!","This lecture has not started yet!");
        } else {
            NavigationController.getCurrentController().goToUserChatPage();

        }
    }

    /**
     * Join lecture as a moderator.
     * @throws IOException exception if something goes wrong
     */
    private void joinAsModerator() throws IOException {
        String modkeyString = modkeyTextField.getText();
        boolean result = LectureCommunication
                .validateModerator(enterLectureCodeTextField.getText(),modkeyString);

        if (!result) {
            alert(Alert.AlertType.ERROR,"Invalid moderator key","Wrong moderator key!");
            return;
        }
        Lecture.getCurrentLecture().setModkey(UUID.fromString(modkeyString));
        NavigationController.getCurrentController().goToLecturerChatPage();
    }

    /**
     * A method that displays an alert.
     * @param type the type of alert to display
     * @param title the title of alert to display
     * @param content the content of alert to display
     */
    private void alert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        //alert.setHeaderText(null);

        alert.setContentText(content);
        alert.showAndWait();
    }


    /**
     * Navigate to the previous scene.
     */
    public void goBack() {

        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }

}
