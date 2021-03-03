package nl.tudelft.oopp.livechat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import nl.tudelft.oopp.livechat.communication.ServerCommunication;

/**
 * Class for controlling events happening from the main scene.
 */
public class MainSceneController {

    @FXML
    private TextField EnterRoomCode;


    /**
     * Creates a lecture and shows a popup.
     */
    public void createALecture() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creating lecture");
        alert.setHeaderText(null);
        alert.setContentText(ServerCommunication
                .createLecture(EnterRoomCode.getCharacters().toString()).toString());
        alert.showAndWait();

        System.out.println("Button worked!");
    }
}
