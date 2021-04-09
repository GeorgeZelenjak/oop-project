package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import nl.tudelft.oopp.livechat.controllers.gui.NavigationController;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class UserManualSceneController {


    @FXML
    private Text helpText;

    /**
     * Goes back to the previous page.
     */
    public void goBack() {
        NavigationController.getCurrent().goBack();
    }

    /**
     * Displays the Create Lecture category.
     */
    public void showCreateLectureText() {
        showCategoryX("client/src/main/resources/textFiles/createLecture.txt");
    }

    /**
     * Displays the Join Lecture category.
     */
    public void showJoinLectureText() {
        showCategoryX("client/src/main/resources/textFiles/joinLecture.txt");
    }

    /**
     * Displays the About Application category.
     */
    public void showAboutApplicationText() {
        showCategoryX("client/src/main/resources/textFiles/about.txt");
    }

    /**
     * Displays the Share Lecture ID category.
     */
    public void showShareLectureIDText() {
        showCategoryX("client/src/main/resources/textFiles/shareLectureID.txt");
    }

    /**
     * Displays the Asking Questions category.
     */
    public void showAskingQuestionsText() {
        showCategoryX("client/src/main/resources/textFiles/askingQuestions.txt");
    }

    /**
     * Displays the Lecturer Mode category.
     */
    public void showLecturerModeText() {
        showCategoryX("client/src/main/resources/textFiles/lecturerMode.txt");
    }

    /**
     * Displays the Question Information category.
     */
    public void showQuestionInfoText() {
        showCategoryX("client/src/main/resources/textFiles/questionInfo.txt");
    }

    /**
     * Displays the Question Handling category.
     */
    public void showQuestionHandlingText() {
        showCategoryX("client/src/main/resources/textFiles/questionHandling.txt");
    }

    /**
     * Displays the Exporting Question category.
     */
    public void showExportingQuestionsText() {
        showCategoryX("client/src/main/resources/textFiles/questionExporting.txt");
    }

    /**
     * Displays the Lecture Speed Voting category.
     */
    public void showLectureSpeedVotingText() {
        showCategoryX("client/src/main/resources/textFiles/lectureSpeedVoting.txt");
    }

    /**
     * Displays the Create Polls category.
     */
    public void showCreatePollsText() {
        showCategoryX("client/src/main/resources/textFiles/createPolls.txt");
    }

    /**
     * Displays the Interacting With Polls category.
     */
    public void showInteractingPollsText() {
        showCategoryX("client/src/main/resources/textFiles/interactWithPolls.txt");
    }

    /**
     * Displays the Banning Users category.
     */
    public void showBanningUsersText() {
        showCategoryX("client/src/main/resources/textFiles/banningUsers.txt");
    }

    /**
     * A helper method that loads a file from a given path, parses it as a string and displays the
     *   requested text of the selected help category.
     * @param fileName the path to the text file
     */
    public void showCategoryX(String fileName) {
        File file = new File(fileName);
        StringBuilder text = new StringBuilder();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine()).append("\n");
            }
            helpText.setText(text.toString());
            helpText.setTextAlignment(TextAlignment.JUSTIFY);

        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        }
    }
}
