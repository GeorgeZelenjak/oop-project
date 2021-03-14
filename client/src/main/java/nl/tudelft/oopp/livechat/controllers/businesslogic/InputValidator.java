package nl.tudelft.oopp.livechat.controllers.businesslogic;

import nl.tudelft.oopp.livechat.controllers.AlertController;

/**
 * Class for the Input validator.
 */
public class InputValidator {
    /**
     * Validate user name.
     *
     * @param name      the name
     * @param maxLength the max length
     * @return the boolean
     */
    public static boolean validateUserName(String name, int maxLength) {
        if (name.length() == 0) {
            AlertController.alertWarning("No name entered",
                    "Please enter your name!");
            return false;
        } else if (name.length() > maxLength) {
            AlertController.alertWarning("Long name",
                    "Your name is too long!\n(max: " + maxLength
                            + " characters, you entered: " + name.length() + ")");
            return false;
        }
        return true;
    }

    /**
     * Validate lecture name.
     *
     * @param name      the name
     * @param maxLength the max length
     * @return the boolean
     */
    public static boolean validateLectureName(String name, int maxLength) {
        if (name.length() == 0) {
            AlertController.alertWarning("Empty lecture name",
                    "Please enter the lecture name!");
            return false;
        } else if (name.length() > maxLength) {
            AlertController.alertWarning("Long lecture name",
                    "The lecture name is too long!\n(max: " + maxLength
                            + " characters, you entered: " + name.length() + ")");
            return false;
        }
        return true;
    }

    /**
     * Validate lecture id input.
     *
     * @param id        the id
     * @param maxLength the max length
     * @return the boolean
     */
    public static boolean validateLectureIdInput(String id, int maxLength) {
        if (id.equals("")) {
            AlertController.alertWarning("No lecture id entered", "Please enter the lecture id!");
            return false;
        } else if (id.length() > maxLength) {
            AlertController.alertWarning(
                    "Too long lecture id", "Lecture id is too long to be valid!");
            return false;
        }
        return true;
    }

    /**
     * Validate mod key input.
     *
     * @param id        the id
     * @param maxLength the max length
     * @return the boolean
     */
    public static boolean validateModKeyInput(String id, int maxLength) {
        if (id.equals("")) {
            AlertController.alertWarning(
                    "No moderator key entered", "Please enter the moderator key!");
            return false;
        } else if (id.length() > maxLength) {
            AlertController.alertWarning(
                    "Too long moderator key", "Moderator key is too long to be valid!");
            return false;
        }
        return true;
    }
}
