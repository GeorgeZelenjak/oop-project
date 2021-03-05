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
                .createLecture(enterRoomName.getCharacters().toString());
        Lecture.setCurrentLecture(lecture); //Sets static current lecture object
        alert.setContentText(lecture.toString());
        alert.showAndWait();

        //Navigation back to the main scene
        Parent root = FXMLLoader.load(getClass().getResource("/mainScene.fxml"));
        Stage window = (Stage) enterRoomName.getScene().getWindow();
        window.setScene(new Scene(root, 600,400));

        System.out.println("Button worked!");


    }

}
