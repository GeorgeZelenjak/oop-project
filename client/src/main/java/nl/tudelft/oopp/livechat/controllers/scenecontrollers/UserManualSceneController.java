package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import nl.tudelft.oopp.livechat.controllers.NavigationController;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Class for the UserManual Scene controller.
 */
public class UserManualSceneController {


    @FXML
    private Text helpText;
    @FXML
    private StackPane helpPane;
    /**
     * Go back to the previous scene.
     */
    public void goBack() {
        NavigationController.getCurrentController().goBack();
        System.out.println("Button was pressed!");
    }

    public void showCreateLectureText() {
        showCategoryX("client/src/main/resources/textFiles/createLecture.txt");
    }

    public void showJoinLectureText() {
        showCategoryX("client/src/main/resources/textFiles/joinLecture.txt");

    }

    public void showAboutApplicationText() {
        showCategoryX("client/src/main/resources/textFiles/about.txt");
    }
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
