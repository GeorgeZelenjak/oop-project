package nl.tudelft.oopp.livechat.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LecturerChatPageController {

    @FXML
    private Button backButton;
    @FXML
    private Button userManual;
    @FXML
    private Button settings;


    /**
     * Go back to main.
     *
     * @throws IOException the io exception
     */
    public void goBackToMain() throws IOException {

        //Navigating back to Main Page

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm your action");
        alert.setHeaderText(null);

        alert.setContentText("Are you sure do you want to quit this lecture?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Parent root = FXMLLoader.load(getClass().getResource("/mainScene.fxml"));
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(new Scene(root, 600,400));
        }



    }


    /**
     * Go to user manual.
     *
     * @throws IOException the io exception
     */
    public void goToUserManual() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/userManual.fxml"));
        Stage window = (Stage) userManual.getScene().getWindow();
        window.setScene(new Scene(root, 1000,650));
    }

    /**
     * Go to settings.
     *
     * @throws IOException the io exception
     */
    public void goToSettings() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/settingsPage.fxml"));
        Stage window = (Stage) settings.getScene().getWindow();
        window.setScene(new Scene(root, 1000,650));
    }

}
