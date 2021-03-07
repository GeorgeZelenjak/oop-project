package nl.tudelft.oopp.livechat.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
import nl.tudelft.oopp.livechat.views.MainSceneDisplay;


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
        Lecture lecture = ServerCommunication
                .createLecture(URLEncoder.encode(
                        enterRoomName.getCharacters().toString(), StandardCharsets.UTF_8));
        //Lecture.setCurrentLecture(lecture); //Sets static current lecture object
        String ret = "";
        try {
            ret = lecture.toString();
        } catch (Exception e) {
            ret = "";
        }
        alert.setContentText(ret);
        alert.showAndWait();

        //Navigation back to the main scene
        NavigationController.getCurrentController().goToMainScene();

        System.out.println(lecture.toString());

    }

}
