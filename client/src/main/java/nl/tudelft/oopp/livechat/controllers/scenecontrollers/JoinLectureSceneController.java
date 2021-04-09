package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import nl.tudelft.oopp.livechat.controllers.gui.AlertController;
import nl.tudelft.oopp.livechat.businesslogic.InputValidator;
import nl.tudelft.oopp.livechat.controllers.gui.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
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
     * Goes to the lecture page if successful.
     */
    public void goToLecture() {
        String name = enterNameTextField.getText();
        int inputStatusUserName = InputValidator.validateLength(name, 50);

        if (inputStatusUserName == -1) {
            AlertController.alertWarning("No name entered", "Please enter your name!");
            return;
        }
        if (!InputValidator.checkBadWords(name)) {
            AlertController.alertError("Curse language", "Your name contains curse language!");
            return;
        }
        if (!InputValidator.checkName(name)) {
            AlertController.alertError("Wrong name", "Please enter your real name!");
            return;
        }
        if (inputStatusUserName == -2) {
            AlertController.alertWarning("Long name", "Your name is too long!\n(max: " + 50
                            + " characters, you entered: "
                            + name.length() + ")");
            return;
        }

        String uuidString = enterLectureCodeTextField.getText();
        int inputStatusLectureId = InputValidator.validateLength(uuidString, 100);

        if (inputStatusLectureId == -1) {
            AlertController.alertWarning("No lecture id entered", "Please enter the lecture id!");
            return;
        }
        if (!InputValidator.validateUUID(uuidString)) {
            AlertController.alertError("Invalid UUID", "Inserted lecture ID is invalid!");
            return;
        }

        User.setUserName(name);

        Lecture.setCurrent(LectureCommunication.joinLectureById(uuidString));
        Lecture currentLecture = Lecture.getCurrent();

        if (currentLecture == null) {
            AlertController.alertError("Lecture joining error.", "No lecture joined!");
        } else if (modkeyCheckBox.isSelected()) {
            joinAsModerator();
        } else {
            joinAsStudent();
        }
    }

    /**
     * Toggles the visibility of the modKeyTextField.
     */
    public void onCheckBoxAction() {
        modkeyTextField.setVisible(!modkeyTextField.isVisible());
    }

    /**
     * Joins lecture as a student.
     */
    private void joinAsStudent() {
        if (!Lecture.getCurrent().isOpen()
                || Lecture.getCurrent().getStartTime().getTime() > System.currentTimeMillis()) {
            AlertController.alertInformation("Lecture not open yet!",
                    "This lecture has not started yet!");
        } else {
            NavigationController.getCurrent().goToUserChatPage();
        }
    }

    /**
     * Joins lecture as a moderator.
     */
    private void joinAsModerator() {
        String modkeyString = modkeyTextField.getText();

        if (InputValidator.validateLength(modkeyTextField.getText(), 300) < 0) {
            AlertController.alertWarning("No moderator key entered",
                    "Please enter the moderator key!");
            return;
        }
        if (!InputValidator.validateUUID(modkeyString)) {
            AlertController.alertWarning("Invalid UUID", "Inserted moderator key is invalid!");
            return;
        }

        if (!LectureCommunication.validateModerator(enterLectureCodeTextField.getText(),
                modkeyString)) {
            return;
        }
        Lecture.getCurrent().setModkey(UUID.fromString(modkeyString));
        NavigationController.getCurrent().goToLecturerChatPage();
    }

    /**
     * Goes to the previous scene.
     */
    public void goBack() {
        NavigationController.getCurrent().goBack();
    }

    /**
     * Goes to the user manual page.
     */
    public void goToUserManualScene() {
        NavigationController.getCurrent().goToUserManual();
    }

}
