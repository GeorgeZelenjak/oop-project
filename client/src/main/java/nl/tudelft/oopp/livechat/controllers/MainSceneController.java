package nl.tudelft.oopp.livechat.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
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

    /** Navigates to Lecture scene (for Students).
     *
     * @throws IOException - in case Stage throws an exception
     */
    public void goToLecture() throws IOException {

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
            Parent root = FXMLLoader.load(getClass().getResource("/userChatPage.fxml"));
            Stage window = (Stage) enterRoomCode.getScene().getWindow();
            window.setScene(new Scene(root, 1000,650));
        }




    }

    /**
     * Go to user manual.
     *
     * @throws IOException the io exception
     */
    public void goToUserManual() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/userManual.fxml"));
        Stage window = (Stage) enterRoomCode.getScene().getWindow();
        window.setScene(new Scene(root, 1000,650));
    }




}
