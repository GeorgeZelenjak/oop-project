package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import nl.tudelft.oopp.livechat.controllers.NavigationController;

/**
 * Class for the UserManual Scene controller.
 */
public class UserManualSceneController {

    /**
     * Go back to the previous scene.
     */
    public void goBack() {
        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }
}
