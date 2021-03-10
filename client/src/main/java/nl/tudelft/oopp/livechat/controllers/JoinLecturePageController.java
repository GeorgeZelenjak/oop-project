package nl.tudelft.oopp.livechat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import nl.tudelft.oopp.livechat.communication.ServerCommunication;
import nl.tudelft.oopp.livechat.data.Lecture;

import java.io.IOException;

public class JoinLecturePageController {


    @FXML
    private TextField modKeyField;

    @FXML
    private TextField enterRoomCode;

    public void onCheckBoxAction(){
        modKeyField.setVisible(!modKeyField.isVisible());
    }

    public void goToLecture() throws IOException {

        Lecture.setCurrentLecture(ServerCommunication.joinLectureById(enterRoomCode.getText()));
        Lecture currentLecture = Lecture.getCurrentLecture();

        if (currentLecture == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error: 404");
            alert.setHeaderText(null);

            alert.setContentText("Invalid Lecture! (404)");
            alert.showAndWait();
        } else if (!currentLecture.isOpen()) {

            //Creating lecture and translating it to String
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lecture not open yet!");
            alert.setHeaderText(null);

            alert.setContentText("This lecture has not started yet!");
            alert.showAndWait();
        } else {

            //Navigating to the scene
            NavigationController.getCurrentController().goToUserChatPage();


        }
    }
    public void goBack() {

        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }

}
