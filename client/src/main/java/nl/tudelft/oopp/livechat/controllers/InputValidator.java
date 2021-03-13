package nl.tudelft.oopp.livechat.controllers;

public class InputValidator {
    public static int validateUserName(String name, int maxLength) {
        if (name.length() == 0) {
            AlertController.alertWarning("No name entered",
                    "Please enter your name!");
            return -1;
        } else if (name.length() > maxLength) {
            AlertController.alertWarning("Long name",
                    "Your name is too long!\n(max 100 characters)");
            return -1;
        }
        return 0;
    }

    public static int validateLectureName(String name, int maxLength) {
        if (name.length() == 0) {
            AlertController.alertWarning("Empty lecture name",
                    "Please enter the lecture name!");
            return -1;
        } else if (name.length() > maxLength) {
            AlertController.alertWarning("Long lecture name",
                    "The lecture name is too long!\n(max 255 characters)");
            return -1;
        }
        return 0;
    }
}
