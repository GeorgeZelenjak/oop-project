package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import nl.tudelft.oopp.livechat.controllers.NavigationController;

public class SettingsSceneController {


    /**
     * Go back to previous Scene.
     */
    public void goBack() {
        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }
}
