package nl.tudelft.oopp.livechat.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.tudelft.oopp.livechat.communication.ServerCommunication;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.views.CreateRoomDisplay;



/**
 * Class for controlling events happening from the main scene.
 */
public class MainSceneController {

    @FXML
    private TextField enterRoomCode;


    /**
     * Navigates to lecture creation scene.
     */
    public void goToCreateLecture() throws IOException {

        //Navigating to the scene
        Parent root = FXMLLoader.load(getClass().getResource("/inputLectureParameters.fxml"));
        Stage window = (Stage) enterRoomCode.getScene().getWindow();
        window.setScene(new Scene(root, 600,400));

    }


}
