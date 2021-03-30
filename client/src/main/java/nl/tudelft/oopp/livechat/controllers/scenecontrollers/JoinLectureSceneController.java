package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.businesslogic.InputValidator;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Class for the JoinLecture Scene controller.
 */
public class JoinLectureSceneController implements Initializable {

    @FXML
    private TextField enterNameTextField;

    @FXML
    private TextField enterLectureCodeTextField;

    @FXML
    private TextField modkeyTextField;

    @FXML
    private CheckBox modkeyCheckBox;

    @FXML
    private Button goBackButton;

    @FXML
    private Button goToJoinLectureButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        goBackButton.setTooltip(new Tooltip("Go back to previous page"));

        goToJoinLectureButton.setTooltip(
                new Tooltip("Joins the lecture either as a student "
                        + "\nor as a lecturer/moderator")
        );

        modkeyTextField.setTooltip(new Tooltip("Check this box if you are a lecturer/moderator"));
    }

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
        String name = enterNameTextField.getText();
        String uuidString = enterLectureCodeTextField.getText();

        int inputStatusUserName = InputValidator.validateLength(name, 50);
        int inputStatusLectureId = InputValidator.validateLength(uuidString, 100);

        if (inputStatusUserName == -1) {
            AlertController.alertWarning("No name entered",
                    "Please enter your name!");
            return;
        }
        if (inputStatusUserName == -2) {
            AlertController.alertWarning("Long name",
                    "Your name is too long!\n(max: " + 50
                            + " characters, you entered: "
                            + enterNameTextField.getText().length() + ")");
            return;
        }
        if (inputStatusLectureId == -1) {
            AlertController.alertWarning("No lecture id entered",
                    "Please enter the lecture id!");
            return;
        }
        if (inputStatusLectureId == -2) {
            AlertController.alertWarning(
                    "Too long lecture id", "Lecture id is too long to be valid!");
            return;
        }

        if (!InputValidator.validateUUID(uuidString)) {
            AlertController.alertError("Invalid UUID", "Inserted lecture ID is invalid!");
            return;
        }

        User.setUserName(name);

        Lecture.setCurrent(
                LectureCommunication.joinLectureById(uuidString));
        Lecture currentLecture = Lecture.getCurrent();

        if (currentLecture == null) {
            System.out.println("no lecture joined");
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
        if (!Lecture.getCurrent().isOpen()) {
            AlertController.alertInformation(
                    "Lecture not open yet!","This lecture has not started yet!");
        } else {
            NavigationController.getCurrent().goToUserChatPage();
        }
    }

    /**
     * Join lecture as a moderator.
     * @throws IOException exception if something goes wrong
     */
    private void joinAsModerator() throws IOException {
        String modkeyString = modkeyTextField.getText();

        int inputStatusModKey = InputValidator.validateLength(
                modkeyTextField.getText(), 255);
        if (inputStatusModKey == -1) {
            AlertController.alertWarning(
                    "No moderator key entered", "Please enter the moderator key!");
            return;
        }
        if (inputStatusModKey == -2) {
            AlertController.alertWarning(
                    "Too long moderator key", "Moderator key is too long to be valid!");
            return;
        }

        if (!InputValidator.validateUUID(modkeyString)) {
            AlertController.alertError("Invalid UUID", "Inserted moderator key is invalid!");
            return;
        }

        if (!LectureCommunication
                .validateModerator(enterLectureCodeTextField.getText(), modkeyString)) {
            return;
        }
        Lecture.getCurrent().setModkey(UUID.fromString(modkeyString));
        NavigationController.getCurrent().goToLecturerChatPage();
    }

    /**
     * Navigate to the previous scene.
     */
    public void goBack() {

        NavigationController.getCurrent().goBack();
        System.out.println("Button was pressed!");
    }

}
