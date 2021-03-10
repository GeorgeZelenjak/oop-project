package nl.tudelft.oopp.livechat.controllers;

public class UserManualController {

    public void goBack() {
        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }


}
