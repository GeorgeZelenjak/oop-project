package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;


public class CreateRoomController {

    @FXML
    private TextField enterRoomName;


    /**
     * Creates the lecture, shows alert with lecture and creator names
     * and returns to the main scene.
     *
     * @throws IOException the io exception
     */
    public void createLecture() throws IOException {

        //Creating lecture and translating it to String
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creating lecture");
        alert.setHeaderText(null);
        Lecture lecture = LectureCommunication
                .createLecture(enterRoomName.getText());
        //Lecture.setCurrentLecture(lecture); //Sets static current lecture object
        String ret;
        try {
            ret = lecture.toString();
        } catch (Exception e) {
            ret = "";
            e.printStackTrace();
        }
        alert.setContentText(ret);
        alert.showAndWait();

        //Navigation back to the main scene
        NavigationController.getCurrentController().goToMainScene();

        System.out.println(lecture.toString());

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
