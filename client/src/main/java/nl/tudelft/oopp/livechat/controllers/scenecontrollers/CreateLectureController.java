package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.businesslogic.InputValidator;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;


/**
 * Class for the CreateLecture Scene controller.
 */
public class CreateLectureController implements Initializable {

    @FXML
    private TextField enterLectureNameTextField;

    @FXML
    private TextField enterYourNameTextField;

    @FXML
    private DatePicker lectureSchedulingDateDatePicker;

    @FXML
    private CheckBox lectureSchedulingCheckBox;

    @FXML
    private TextField lectureScheduleHourTextField;

    @FXML
    private TextField lectureScheduleMinuteTextField;

    @FXML
    private Text dotsText;

    @FXML
    private Button goToHelpButton;

    @FXML
    private Button goToSettingsButton;

    @FXML
    private Button goBackButton;

    @FXML
    private Button createLectureButton;

    @FXML
    private TextField questionDelay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        goToHelpButton.setTooltip(new Tooltip("Open Help & Documentation Page"));
        goToSettingsButton.setTooltip(new Tooltip("Open Settings Page"));

        goBackButton.setTooltip(new Tooltip("Go back to previous page"));
        createLectureButton.setTooltip(new Tooltip("Creates a new lecture and "
                + "\nnavigates to the lecture page"));
    }

    /**
     * Creates the lecture, shows alert with lecture and creator names
     * and returns to the main scene.
     * @throws IOException the io exception
     */
    private void createLecture() throws IOException {
        int inputStatusUserName = InputValidator.validateLength(
                enterYourNameTextField.getText(), 50);
        int inputStatusLectureName = InputValidator.validateLength(
                enterLectureNameTextField.getText(), 255);
        if (inputStatusUserName == -1) {
            AlertController.alertWarning("No name entered",
                    "Please enter your name!");
            return;
        }
        if (inputStatusUserName == -2) {
            AlertController.alertWarning("Long name",
                    "Your name is too long!\n(max: " + 50
                            + " characters, you entered: "
                            + enterYourNameTextField.getText().length() + ")");
            return;
        }
        if (inputStatusLectureName == -1) {
            AlertController.alertWarning("Long lecture name",
                    "The lecture name is too long!\n(max: " + 255
                            + " characters, you entered: "
                            + enterLectureNameTextField.getText().length() + ")");
            return;
        }
        if (inputStatusLectureName == -2) {
            AlertController.alertWarning("Long name",
                    "Your name is too long!\n(max: " + 50
                            + " characters, you entered: "
                            + enterYourNameTextField.getText().length() + ")");
            return;
        }

        if (lectureSchedulingCheckBox.isSelected()) {
            createLectureScheduled();
            return;
        }
        int frequency = 60;
        try {
            if (questionDelay.getText() != null && questionDelay.getText().length() > 0) {
                int delay = Integer.parseInt(questionDelay.getText());
                frequency = delay;
            }

        } catch (NumberFormatException e) {
            String alert = "Invalid input. Please enter a number (in seconds) and try again.";

            AlertController.alertError("Invalid input", alert);
            return;
        }
        User.setUserName(enterYourNameTextField.getText());

        Lecture lecture = LectureCommunication
                .createLecture(enterLectureNameTextField.getText(),
                enterYourNameTextField.getText(), new Timestamp(System.currentTimeMillis()), frequency);

        if (lecture == null) {
            return;
        }




        String alertText = "The lecture has been created successfully!"
                + "\nPress OK to go to the lecture page.";
        AlertController.alertInformation("Creating lecture", alertText);



        Lecture.setCurrentLecture(lecture);
        NavigationController.getCurrentController().goToLecturerChatPage();
        System.out.println(Lecture.getCurrentLecture().getFrequency());
    }

    private void createLectureScheduled() throws IOException {

        if (InputValidator.validateHour(lectureScheduleHourTextField.getText()) != 0
                || InputValidator.validateMinute(lectureScheduleMinuteTextField.getText()) != 0
                || lectureSchedulingDateDatePicker.getValue() == null)  {
            AlertController.alertWarning("Incorrect input", "Provided date or time is invalid!");
            return;
        }

        int hour = Integer.parseInt(lectureScheduleHourTextField.getText());
        int minute = Integer.parseInt(lectureScheduleMinuteTextField.getText());
        Timestamp timestamp = Timestamp.valueOf(lectureSchedulingDateDatePicker
                                    .getValue().atTime(hour,minute));
        int frequency = 60;
        try {
            if (questionDelay.getText() != null && questionDelay.getText().length() > 0) {
                int delay = Integer.parseInt(questionDelay.getText());
                frequency = delay;
            }
        } catch (NumberFormatException e) {
            String alert = "Invalid input. Please enter a number (in seconds) and try again.";

            AlertController.alertError("Invalid input", alert);
            return;
        }
        Lecture lecture = LectureCommunication
                .createLecture(enterLectureNameTextField.getText(),
                        enterYourNameTextField.getText(), timestamp, frequency);

        if (lecture == null) {
            return;
        }


        String alertText = "The lecture has been scheduled successfully!"
                + "\nPress OK to go to the lecture page.";
        String alertText2 = "\n!!!Please copy the moderator "
                + "key to later use it when joining as moderator!!!";
        AlertController.alertInformation("Creating lecture", alertText);
        AlertController.alertWarning("ModKey Warning", alertText2.toUpperCase(Locale.ROOT));



        Lecture.setCurrentLecture(lecture);
        User.setUserName(enterYourNameTextField.getText());
        NavigationController.getCurrentController().goToLecturerChatPage();
        System.out.println(lecture);

        System.out.println(Lecture.getCurrentLecture().getFrequency());
    }

    /**
     * Create lecture when you press the button.
     *
     * @throws IOException the io exception
     */
    public void createLectureButton() throws IOException {
        createLecture();
    }

    /**
     * Create the lecture when you press enter.
     *
     * @throws IOException the io exception
     */
    public void createLectureEnter() throws IOException {
        createLecture();
    }

    /**
     * Go back to previous Scene.
     */
    public void goBack() {
        NavigationController.getCurrentController().goBack();
    }

    /**
     * Go to settings Scene.
     *
     * @throws IOException the io exception
     */
    public void goToSettings() throws IOException {
        NavigationController.getCurrentController().goToSettings();
    }

    /**
     * Go to user manual Scene.
     *
     * @throws IOException the io exception
     */
    public void goToUserManual() throws IOException {
        NavigationController.getCurrentController().goToUserManual();
    }

    /**
     * Hides everything concerning lecture scheduling.
     */
    public void hideLectureScheduling() {
        lectureScheduleMinuteTextField.setDisable(!lectureSchedulingCheckBox.isSelected());
        lectureScheduleMinuteTextField.setVisible(lectureSchedulingCheckBox.isSelected());
        lectureScheduleHourTextField.setDisable(!lectureSchedulingCheckBox.isSelected());
        lectureScheduleHourTextField.setVisible(lectureSchedulingCheckBox.isSelected());
        lectureSchedulingDateDatePicker.setDisable(!lectureSchedulingCheckBox.isSelected());
        lectureSchedulingDateDatePicker.setVisible(lectureSchedulingCheckBox.isSelected());
    }
}
