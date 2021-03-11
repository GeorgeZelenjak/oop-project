package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Optional;

public class LecturerChatSceneController {

    /**
     * Copy lecture id to clipboard.
     */
    public void copyLectureId() {
        String myString = Lecture.getCurrentLecture().getUuid().toString();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Copied successfully!");
        alert.setContentText("Lecture id copied to clipboard");
        alert.show();
    }

    /**
     * Copy moderator key to clipboard.
     */
    public void copyModKey() {
        String myString = Lecture.getCurrentLecture().getModkey().toString();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Copied successfully!");
        alert.setHeaderText(null);
        alert.setContentText("Moderator key copied to clipboard");
        alert.show();
    }

    /**
     * Go back to main page.
     */
    public void goBackToMain() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm your action");
        alert.setHeaderText(null);

        alert.setContentText("Are you sure do you want to quit this lecture?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NavigationController.getCurrentController().goBack();
            NavigationController.getCurrentController().goBack();
        }
    }

    /**
     * Go to user manual.
     * @throws IOException the io exception
     */
    public void goToUserManual() throws IOException {

        NavigationController.getCurrentController().goToUserManual();
    }

    /**
     * Go to settings.
     *
     * @throws IOException the io exception
     */
    public void goToSettings() throws IOException {
        NavigationController.getCurrentController().goToSettings();
    }

    /**
     * Close lecture.
     * @throws IOException the io exception
     */
    public void closeLecture() throws IOException {
        LectureCommunication.closeLecture(Lecture.getCurrentLecture().getUuid().toString(),
                Lecture.getCurrentLecture().getModkey().toString());
        NavigationController.getCurrentController().goToMainScene();
    }
}
