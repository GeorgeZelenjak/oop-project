package nl.tudelft.oopp.livechat.controllers;

public class SettingsPageController {


    /**
     * Go back to previous Scene.
     */
    public void goBack() {

        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }
}
