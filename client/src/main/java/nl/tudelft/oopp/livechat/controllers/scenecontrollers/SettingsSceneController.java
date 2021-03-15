package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import nl.tudelft.oopp.livechat.controllers.NavigationController;

/**
 * Class for the Settings Scene controller.
 */
public class SettingsSceneController {


    /**
     * Go back to previous Scene.
     */
    public void goBack() {
        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }

}
