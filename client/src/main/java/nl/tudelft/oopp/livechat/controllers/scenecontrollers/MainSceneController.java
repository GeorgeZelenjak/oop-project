package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import nl.tudelft.oopp.livechat.controllers.gui.NavigationController;


public class MainSceneController implements Initializable {

    @FXML
    private Button goToJoinLectureButton;

    @FXML
    private Button goToCreateLectureButton;

    @FXML
    private Button goToHelpButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        goToCreateLectureButton.setTooltip(new Tooltip("Go to Create Lecture Page"));
        goToJoinLectureButton.setTooltip(new Tooltip("Go to Join Lecture Page"));

        goToHelpButton.setTooltip(new Tooltip("Open Help & Documentation page"));

    }

    /**
     * Navigates to lecture creation scene.

     */
    public void goToCreateLecture() {
        NavigationController.getCurrent().goToCreateRoomScene();
    }

    /**
     * Navigates to Lecture scene (for Students).
     */
    public void goToLecture() {
        NavigationController.getCurrent().goToJoinLecturePage();
    }

    /**
     * Go to user manual.
     */
    public void goToUserManual() {
        NavigationController.getCurrent().goToUserManual();
    }

    /**
     * Go to debug scene.
     */
    public void goToDebug() {
        NavigationController.getCurrent().goToDebugScene();
    }
}
