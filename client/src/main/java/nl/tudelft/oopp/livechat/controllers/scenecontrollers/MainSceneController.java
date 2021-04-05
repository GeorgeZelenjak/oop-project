package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
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
    private Button goToHelpButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        goToCreateLectureButton.setTooltip(new Tooltip("Go to Create Lecture Page"));
        goToJoinLectureButton.setTooltip(new Tooltip("Go to Join Lecture Page"));

        goToHelpButton.setTooltip(new Tooltip("Open Help & Documentation page"));

    }

    /**
     * Navigates to lecture creation scene.
     *
     * @throws IOException the io exception
     */
    public void goToCreateLecture() throws IOException {
        NavigationController.getCurrent().goToCreateRoomScene();
    }

    /**
     * Navigates to Lecture scene (for Students).
     *
     * @throws IOException - in case Stage throws an exception
     */
    public void goToLecture() throws IOException {
        NavigationController.getCurrent().goToJoinLecturePage();
    }

    /**
     * Go to user manual.
     *
     * @throws IOException the io exception
     */
    public void goToUserManual() throws IOException {
        NavigationController.getCurrent().goToUserManual();
    }

    //    /**
    //     * Go to settings.
    //     *
    //     * @throws IOException the io exception
    //     */
    //    public void goToSettings() throws IOException {
    //        NavigationController.getCurrent().goToSettings();
    //    }

    /**
     * Go to debug scene.
     *
     * @throws IOException the io exception
     */
    public void goToDebug() throws IOException {
        NavigationController.getCurrent().goToDebugScene();
    }
}
