package nl.tudelft.oopp.livechat.controllers;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nl.tudelft.oopp.livechat.communication.ServerCommunication;
import nl.tudelft.oopp.livechat.data.Lecture;


/**
 * The type User chat page controller.
 */
public class UserChatPageController {

    @FXML
    private Button joinRoom;
    @FXML
    private static Text lectureName;


    /**
     * Go back to main.
     *
     * @throws IOException the io exception
     */
    public void goBackToMain() throws IOException {

        //Navigating back to Main Page

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm your action");
        alert.setHeaderText(null);

        alert.setContentText("Are you sure do you want to quit this lecture?");


        //TODO doesnt work-> throws InvocationTargetException && NullPointerException
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Parent root = FXMLLoader.load(getClass().getResource("/mainScene.fxml"));
            Stage window = (Stage) lectureName.getScene().getWindow();
            window.setScene(new Scene(root, 600,400));
        }



    }

    @FXML
    public static void setLectureNameUserPage() {

        lectureName.setText(Lecture.getCurrentLecture().getName());
    }



}
