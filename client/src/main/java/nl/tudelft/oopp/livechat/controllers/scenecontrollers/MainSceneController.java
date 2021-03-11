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

    /**
     * Navigates to lecture creation scene.
     */

    public void goToCreateLecture() throws IOException {
        NavigationController.getCurrentController().goToCreateRoomScene();
    }

    /** Navigates to Lecture scene (for Students).
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




}
