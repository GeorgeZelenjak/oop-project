package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;



public class CreateLectureController {

    @FXML
    private TextField enterLectureNameTextField;

    /**
     * Creates the lecture, shows alert with lecture and creator names
     * and returns to the main scene.
     * @throws IOException the io exception
     */
    private void createLecture() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creating lecture");
        alert.setHeaderText(null);
        Lecture lecture = LectureCommunication
                .createLecture(enterLectureNameTextField.getText());

        String ret;
        try {
            if (lecture == null) {
                throw new IllegalArgumentException("Lecture is null!");
            }
            ret = lecture.toString();
        } catch (Exception e) {
            ret = "";
            e.printStackTrace();
        }
        alert.setContentText("The lecture has been created successfully!\n"
                + "Press OK to go to the lecture page.");
        alert.showAndWait();

        NavigationController.getCurrentController().goToLecturerChatPage();
        Lecture.setCurrentLecture(lecture);
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

    public void goToSettings() throws IOException {
        NavigationController.getCurrentController().goToSettings();
    }

    public void goToUserManual() throws IOException {
        NavigationController.getCurrentController().goToUserManual();
    }

}
