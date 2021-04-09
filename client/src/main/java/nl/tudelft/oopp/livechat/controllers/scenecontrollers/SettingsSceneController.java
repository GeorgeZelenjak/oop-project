package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import nl.tudelft.oopp.livechat.controllers.gui.NavigationController;

/**
 * Class for the Settings Scene controller.
 */
public class SettingsSceneController {


    /**
     * Go back to previous Scene.
     */
    public void goBack() {
        NavigationController.getCurrent().goBack();
    }

}
