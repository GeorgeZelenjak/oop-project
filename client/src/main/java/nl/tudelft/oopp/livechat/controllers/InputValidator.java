package nl.tudelft.oopp.livechat.controllers;

public class InputValidator {
    public static int validateUserName(String name, int maxLength) {
        if (name.length() == 0) {
            AlertController.alertWarning("No name entered",
                    "Please enter your name!");
            return -1;
        } else if (name.length() > maxLength) {
            AlertController.alertWarning("Long name",
                    "Your name is too long!\n(max: " + maxLength
                            + " characters, you entered: " + name.length() + ")");
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
                    "The lecture name is too long!\n(max: " + maxLength
                            + " characters, you entered: " + name.length() + ")");
            return -1;
        }
        return 0;
    }

    public static int validateLectureIdInput(String id, int maxLength) {
        if (id.equals("")) {
            AlertController.alertWarning("No lecture id entered", "Please enter the lecture id!");
            return -1;
        } else if (id.length() > maxLength) {
            AlertController.alertWarning("Too long lecture id", "Lecture id is too long to be valid!");
            return -1;
        }
        return 0;
    }

    public static int validateModKeyInput(String id, int maxLength) {
        if (id.equals("")) {
            AlertController.alertWarning("No moderator key entered", "Please enter the moderator key!");
            return -1;
        } else if (id.length() > maxLength) {
            AlertController.alertWarning("Too long moderator key", "Moderator key is too long to be valid!");
            return -1;
        }
        return 0;
    }
}
