package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import nl.tudelft.oopp.livechat.controllers.gui.AlertController;
import nl.tudelft.oopp.livechat.businesslogic.InputValidator;
import nl.tudelft.oopp.livechat.controllers.gui.NavigationController;
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
    private Button goBackButton;

    @FXML
    private Button createLectureButton;

    @FXML
    private TextField questionDelay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        goToHelpButton.setTooltip(new Tooltip("Open Help & Documentation Page"));

        goBackButton.setTooltip(new Tooltip("Go back to previous page"));
        createLectureButton.setTooltip(new Tooltip("Creates a new lecture and "
                + "\nnavigates to the lecture page"));

        lectureSchedulingDateDatePicker.setEditable(false);
    }

    /**
     * Creates a lecture and goes to the lecture page if everything is successful.
     */
    private void createLecture() {
        String name = enterYourNameTextField.getText();
        String lectureName = enterLectureNameTextField.getText();

        int inputStatusUserName = InputValidator.validateLength(name, 50);
        int inputStatusLectureName = InputValidator.validateLength(lectureName, 50);

        if (inputStatusUserName == -1) {
            AlertController.alertWarning("No name entered", "Please enter your name!");
            return;
        }
        if (inputStatusUserName == -2) {
            AlertController.alertWarning("Long name", "Your name is too long!\n(max: "
                    + 50 + " characters, you entered: " + name.length() + ")");
            return;
        }
        if (inputStatusLectureName == -1) {
            AlertController.alertWarning("No name entered", "Please enter the lecture name!");
            return;
        }
        if (inputStatusLectureName == -2) {
            AlertController.alertWarning("Long name", "The lecture name is too long!\n(max: "
                    + 150 + " characters, you entered: " + lectureName.length() + ")");
            return;
        }

        User.setUserName(name);
        int frequency = setFrequency();

        if (lectureSchedulingCheckBox.isSelected()) {
            createLectureScheduled(name, lectureName, frequency);
            return;
        }

        Lecture lecture = LectureCommunication.createLecture(lectureName, name,
                        new Timestamp(System.currentTimeMillis()), frequency);
        if (lecture == null) return;

        AlertController.alertInformation("Lecture created",
                "The lecture has been created successfully!"
                            + "\nPress OK to go to the lecture page.");

        Lecture.setCurrent(lecture);
        NavigationController.getCurrent().goToLecturerChatPage();
    }

    /**
     * Creates a scheduled lecture and goes to the lecture page if everything is successful.
     * @param name the name of the creator
     * @param lectureName the name of the lecture
     * @param frequency the frequency of asking questions
     */
    private void createLectureScheduled(String name, String lectureName, int frequency) {
        int hour = InputValidator.validateHour(lectureScheduleHourTextField.getText());
        int minute = InputValidator.validateMinute(lectureScheduleMinuteTextField.getText());

        if (hour < 0 || minute < 0 || lectureSchedulingDateDatePicker.getValue() == null)  {
            AlertController.alertWarning("Incorrect input", "Provided date or time is invalid!");
            return;
        }

        Timestamp timestamp = Timestamp.valueOf(lectureSchedulingDateDatePicker
                                    .getValue().atTime(hour,minute));

        Lecture lecture = LectureCommunication.createLecture(lectureName,
                name, timestamp, frequency);
        if (lecture == null) return;

        AlertController.alertInformation("Lecture created", "The lecture has been scheduled"
                + " successfully!\nPress OK to go to the lecture page.");
        AlertController.alertWarning("ModKey Warning", "\n!Please copy the moderator "
                + "key to later use it when joining as moderator!".toUpperCase(Locale.ROOT));

        Lecture.setCurrent(lecture);
        NavigationController.getCurrent().goToLecturerChatPage();
    }

    /**
     * Samples the frequency of asking questions.
     * @return the frequency of asking questions: the selected one if it is a number
     *      between 0 and 300, or the default value 60 if it is invalid
     */
    private int setFrequency() {
        int frequency = 60;
        if (questionDelay.getText() != null && questionDelay.getText().length() > 0) {
            frequency = InputValidator.validateFrequency(questionDelay.getText());
            if (frequency < 0 || frequency > 300) {
                AlertController.alertError("Invalid input", "The frequency must be a positive "
                        + "number between 0 and 300. The default value 60 is set.\n "
                        + "You can change it later if you wish");
                return 60;
            }
        }
        return frequency;
    }

    /**
     * Creates a lecture when the user presses the button.
     */
    public void createLectureButton() {
        createLecture();
    }

    /**
     * Creates a lecture when you the user presses ENTER.
     */
    public void createLectureEnter() {
        createLecture();
    }

    /**
     * Goes back to the previous page.
     */
    public void goBack() {
        NavigationController.getCurrent().goBack();
    }

    /**
     * Goes to user manual page.
     */
    public void goToUserManual() {
        NavigationController.getCurrent().goToUserManual();
    }

    /**
     * Hides everything concerning lecture scheduling.
     */
    public void hideLectureScheduling() {
        lectureScheduleMinuteTextField.setDisable(!lectureSchedulingCheckBox.isSelected());
        lectureScheduleMinuteTextField.setVisible(lectureSchedulingCheckBox.isSelected());
        lectureScheduleHourTextField.setDisable(!lectureSchedulingCheckBox.isSelected());
        dotsText.setVisible(lectureSchedulingCheckBox.isSelected());
        lectureScheduleHourTextField.setVisible(lectureSchedulingCheckBox.isSelected());
        lectureSchedulingDateDatePicker.setDisable(!lectureSchedulingCheckBox.isSelected());
        lectureSchedulingDateDatePicker.setVisible(lectureSchedulingCheckBox.isSelected());
    }
}
