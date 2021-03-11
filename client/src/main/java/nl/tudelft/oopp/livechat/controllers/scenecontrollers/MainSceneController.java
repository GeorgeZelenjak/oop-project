package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import nl.tudelft.oopp.livechat.controllers.NavigationController;

import java.io.IOException;


/**
 * Class for controlling events happening from the main scene.
 */
public class MainSceneController {

    @FXML
    TextField enterRoomCode;
    @FXML
    Button  joinRoom;

    /**
     * Navigates to lecture creation scene.
     */

    public void goToCreateLecture() throws IOException {

        //Navigating to the scene
        NavigationController.getCurrentController().goToCreateRoomScene();

    }

    /** Navigates to Lecture scene (for Students).
     *
     * @throws IOException - in case Stage throws an exception
     */

    public void goToLecture() throws IOException {
        NavigationController.getCurrentController().goToJoinLecturePage();

        /*
        Lecture.setCurrentLecture(ServerCommunication.joinLectureById(enterRoomCode.getText()));
        Lecture currentLecture = Lecture.getCurrentLecture();

        if (currentLecture == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error: 404");
            alert.setHeaderText(null);

            alert.setContentText("Invalid Lecture! (404)");
            alert.showAndWait();
        } else if (!currentLecture.isOpen()) {

            //Creating lecture and translating it to String
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lecture not open yet!");
            alert.setHeaderText(null);

            alert.setContentText("This lecture has not started yet!");
            alert.showAndWait();
        } else {

            //Navigating to the scene
            NavigationController.getCurrentController().goToUserChatPage();


        }*/


    }

    /**
     * Go to user manual.
     *
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




}
