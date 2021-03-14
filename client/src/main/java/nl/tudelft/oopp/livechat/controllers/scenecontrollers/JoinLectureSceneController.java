package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.controllers.InputValidator;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;

import java.io.IOException;
import java.util.UUID;

/**
 * Class for the JoinLecture Scene controller.
 */
public class JoinLectureSceneController {

    @FXML
    private TextField enterNameTextField;

    @FXML
    private TextField enterLectureCodeTextField;

    @FXML
    private TextField modkeyTextField;

    @FXML
    private CheckBox modkeyCheckBox;


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
        if (!InputValidator.validateUserName(enterNameTextField.getText(), 50)
                || !InputValidator.validateLectureIdInput(
                        enterLectureCodeTextField.getText(), 100)) {
            return;
        }

        User.setUserName(enterNameTextField.getText());

        Lecture.setCurrentLecture(
                LectureCommunication.joinLectureById(enterLectureCodeTextField.getText()));
        Lecture currentLecture = Lecture.getCurrentLecture();

        if (currentLecture == null) {
            AlertController.alertError("Error", "Lecture was not found.");

        } else if (modkeyCheckBox.isSelected()) {
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
            AlertController.alertInformation(
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
        if (!InputValidator.validateModKeyInput(modkeyString, 200)) {
            return;
        }
        boolean result = LectureCommunication
                .validateModerator(enterLectureCodeTextField.getText(),modkeyString);

        if (!result) {
            AlertController.alertError("Invalid moderator key","Wrong moderator key!");
            return;
        }
        Lecture.getCurrentLecture().setModkey(UUID.fromString(modkeyString));
        NavigationController.getCurrentController().goToLecturerChatPage();
    }

    /**
     * Navigate to the previous scene.
     */
    public void goBack() {

        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }

}
