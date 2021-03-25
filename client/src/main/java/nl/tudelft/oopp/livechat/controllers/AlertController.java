package nl.tudelft.oopp.livechat.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Class for the Alert controller.
 */
public class AlertController {

    /**
     * Sets a WARNING alert with specified title and text.
     *
     * @param title   the title
     * @param content the content
     */
    public static void alertWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);

        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Sets an INFORMATION alert with specified title and text.
     *
     * @param title   the title
     * @param content the content
     */
    public static void alertInformation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Sets a CONFIRMATION alert with specified title and text.
     *
     * @param title   the title
     * @param content the content
     */
    public static boolean alertConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Sets an alert without type with specified title and text.
     *
     * @param title   the title
     * @param content the content
     */
    public static void alertNone(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(null);

        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Sets an ERROR alert with specified title and text.
     *
     * @param title   the title
     * @param content the content
     */
    public static void alertError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);

        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Returns an alert with specified type, title and text.
     *
     * @param alertType the alert type
     * @param title     the title
     * @param content   the content
     * @return the alert
     */
    public static Alert createAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);

        alert.setContentText(content);
        return alert;
    }
}
