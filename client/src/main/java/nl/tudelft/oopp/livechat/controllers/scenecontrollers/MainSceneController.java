package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import nl.tudelft.oopp.livechat.controllers.NavigationController;


/**
 * Class for Main Scene controller.
 */
public class MainSceneController implements Initializable {

    @FXML
    private Button goToJoinLectureButton;

    @FXML
    private Button goToCreateLectureButton;

    @FXML
    private Button goToExportQAFromLectureButton;

    @FXML
    private Button goToSettingsButton;

    @FXML
    private Button goToHelpButton;

    @FXML
    private Button goToRestoreLectureButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        goToCreateLectureButton.setTooltip(new Tooltip("Go to Create Lecture Page"));
        goToJoinLectureButton.setTooltip(new Tooltip("Go to Join Lecture Page"));

        goToExportQAFromLectureButton.setTooltip(new Tooltip("Export Questions and Answers"));
        goToSettingsButton.setTooltip(new Tooltip("Open Settings page"));

        goToHelpButton.setTooltip(new Tooltip("Open Help & Documentation page"));
        goToRestoreLectureButton.setTooltip(new Tooltip("Open the Restore Lecture Page"));

    }

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
