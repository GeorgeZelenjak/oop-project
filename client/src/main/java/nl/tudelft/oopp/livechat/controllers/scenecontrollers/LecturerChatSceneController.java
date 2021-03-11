package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import nl.tudelft.oopp.livechat.controllers.NavigationController;

import java.io.IOException;
import java.util.Optional;

public class LecturerChatSceneController {

    @FXML
    private Button backButton;
    @FXML
    private Button userManual;
    @FXML
    private Button settings;


    /**
     * Go back to main.
     *
     */
    public void goBackToMain() {

        //Navigating back to Main Page

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm your action");
        alert.setHeaderText(null);

        alert.setContentText("Are you sure do you want to quit this lecture?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NavigationController.getCurrentController().goBack();
        }



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
