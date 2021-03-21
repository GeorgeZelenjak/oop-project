package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import nl.tudelft.oopp.livechat.controllers.NavigationController;


/**
 * Class for Main Scene controller.
 */
public class MainSceneController {


    /**
     * Navigates to lecture creation scene.
     *
     * @throws IOException the io exception
     */
    public void goToCreateLecture() throws IOException {
        NavigationController.getCurrentController().goToCreateRoomScene();
    }

    /**
     * Navigates to Lecture scene (for Students).
     *
     * @throws IOException - in case Stage throws an exception
     */
    public void goToLecture() throws IOException {
        NavigationController.getCurrentController().goToJoinLecturePage();
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

    /**
     * Go to debug scene.
     *
     * @throws IOException the io exception
     */
    public void goToDebug() throws IOException {
        NavigationController.getCurrentController().goToDebugScene();
    }
}
