package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.businesslogic.InputValidator;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;


/**
 * Class for the CreateLecture Scene controller.
 */
public class CreateLectureController {

    @FXML
    private TextField enterLectureNameTextField;

    @FXML
    private TextField enterYourNameTextField;

    /**
     * Creates the lecture, shows alert with lecture and creator names
     * and returns to the main scene.
     * @throws IOException the io exception
     */
    private void createLecture() throws IOException {
        if (!InputValidator.validateUserName(enterYourNameTextField.getText(), 50)
                || !InputValidator.validateLectureName(
                        enterLectureNameTextField.getText(), 255)) {
            return;
        }

        Lecture lecture = LectureCommunication
                .createLecture(enterLectureNameTextField.getText());

        if (lecture == null) {
            return;
        }

        String alertText = "The lecture has been created successfully!"
                + "\nPress OK to go to the lecture page.";
        AlertController.alertInformation("Creating lecture", alertText);

        Lecture.setCurrentLecture(lecture);
        User.setUserName(enterYourNameTextField.getText());
        NavigationController.getCurrentController().goToLecturerChatPage();
        System.out.println(lecture);
    }

    /**
     * Create lecture when you press the button.
     *
     * @throws IOException the io exception
     */
    public void createLectureButton() throws IOException {
        createLecture();
    }

    /**
     * Create the lecture when you press enter.
     *
     * @throws IOException the io exception
     */
    public void createLectureEnter() throws IOException {
        createLecture();
    }

    /**
     * Go back to previous Scene.
     */
    public void goBack() {
        NavigationController.getCurrentController().goBack();
    }

    /**
     * Go to settings Scene.
     *
     * @throws IOException the io exception
     */
    public void goToSettings() throws IOException {
        NavigationController.getCurrentController().goToSettings();
    }

    /**
     * Go to user manual Scene.
     *
     * @throws IOException the io exception
     */
    public void goToUserManual() throws IOException {
        NavigationController.getCurrentController().goToUserManual();
    }

}
