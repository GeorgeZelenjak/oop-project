package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.io.IOException;
import nl.tudelft.oopp.livechat.controllers.NavigationController;


/**
 * Class for Main Scene controller.
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

    /**
     * Go to debug scene.
     *
     * @throws IOException the io exception
     */
    public void goToDebugScene() throws IOException {
        NavigationController.getCurrentController().goToDebugScene();
    }
}
