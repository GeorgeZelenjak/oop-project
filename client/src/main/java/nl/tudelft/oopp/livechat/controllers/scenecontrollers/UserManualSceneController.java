package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class for the UserManual Scene controller.
 */
public class UserManualSceneController {


    @FXML
    private Text helpText;

    /**
     * Go back to the previous scene.
     */
    public void goBack() {
        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }

    /** Displays the Create Lecture Category.
     *
     */
    public void showCreateLectureText() {
        showCategoryX("client/src/main/resources/textFiles/createLecture.txt");
    }

    /** Displays the Join Lecture Category.
     *
     */
    public void showJoinLectureText() {
        showCategoryX("client/src/main/resources/textFiles/joinLecture.txt");
    }

    /** Displays the About Application Category.
     *
     */
    public void showAboutApplicationText() {
        showCategoryX("client/src/main/resources/textFiles/about.txt");
    }

    /** Displays the Share Lecture ID Category.
     *
     */
    public void showShareLectureIDText() {
        showCategoryX("client/src/main/resources/textFiles/shareLectureID.txt");
    }

    /** Displays the Asking Questions Category.
     *
     */
    public void showAskingQuestionsText() {
        showCategoryX("client/src/main/resources/textFiles/askingQuestions.txt");
    }

    /** Displays the Lecturer Mode Category.
     *
     */
    public void showLecturerModeText() {
        showCategoryX("client/src/main/resources/textFiles/lecturerMode.txt");
    }

    /** Displays the Question Information Category.
     *
     */
    public void showQuestionInfoText() {
        showCategoryX("client/src/main/resources/textFiles/questionInfo.txt");
    }

    /** Helper Method that loads a File from a given path, parses it as a string and displays the
     *      requested text of the selected help category. This method catches an FileNotFoundException
     *      in case the filepath is incorrect or file is not found.
     * @param fileName - String representing the path of the text file
     */
    public void showCategoryX(String fileName)  {
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
            e.printStackTrace();
        }
    }
}
