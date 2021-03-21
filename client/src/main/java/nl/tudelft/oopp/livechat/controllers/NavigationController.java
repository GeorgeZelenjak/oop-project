package nl.tudelft.oopp.livechat.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

/**
 * Class for navigation between scenes.
 * For navigation use:
 * NavigationController.getCurrentController().goToWhateverPage9();
 */
public class NavigationController {

    private Scene main;

    private static NavigationController currentController;

    private final Stack<Scene> backStack;


    /**
     * Instantiates a new Navigation controller.
     *
     * @param main The main scene of the application
     */
    public NavigationController(Scene main) {
        this.main = main;
        this.backStack = new Stack<>();
    }

    /**
     * Sets current controller.
     *
     * @param currentController the current controller
     */
    public static void setCurrentController(NavigationController currentController) {
        NavigationController.currentController = currentController;
    }

    /**
     * Gets current controller.
     *
     * @return the current controller
     */
    public static NavigationController getCurrentController() {
        return currentController;
    }

    /**
     * Navigation to the main page.
     */
    public void goToUserManual()  {
        goToSceneHelper("/fxml/userManualScene.fxml");
    }

    /**
     * Navigation to the main scene.
     */
    public void goToMainScene()  {
        goToSceneHelper("/fxml/mainScene.fxml");
    }

    /**
     * Navigation to the create room scene.
     */
    public void goToCreateRoomScene()  {
        goToSceneHelper("/fxml/createLectureScene.fxml");
    }

    /**
     * Navigation to the create room scene.
     */
    public void goToJoinLecturePage()  {
        goToSceneHelper("/fxml/joinLectureScene.fxml");
    }

    /**
     * Navigation to the lecturer chat room scene.
     */
    public void goToLecturerChatPage() {
        goToSceneHelper("/fxml/lecturerChatScene.fxml");
    }

    /**
     * Navigation to the user chat page.
     */
    public void goToUserChatPage() {
        goToSceneHelper("/fxml/userChatScene.fxml");
    }

    /**
     * Go to scene helper.
     */
    private void goToSceneHelper(String javaFxFile) {
        try {
            backStack.push(this.main);
            Parent root = FXMLLoader.load(getClass().getResource(javaFxFile));
            Stage window = (Stage) main.getWindow();
            Scene main = new Scene(root, 1080, 768);
            this.main = main;
            window.setScene(main);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popupHelper(String javaFxFile, int width, int height) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource(javaFxFile));
            Stage stage = new Stage();
            stage.setTitle("My New Stage Title");
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Go to settings.
     */
    public void goToSettings() {
        goToSceneHelper("/fxml/settingsScene.fxml");
    }

    /**
     * Go to test scene.
     */
    @SuppressWarnings("unused")
    public void goToTestScene() {
        goToSceneHelper("/fxml/newUserChatScene.fxml");
    }

    public void goToDebugScene() {
        goToSceneHelper("/fxml/debugScene.fxml");
    }

    /**
     * Navigates to the previous scene.
     */
    public void goBack() {
        Stage window = (Stage) main.getWindow();
        this.main = backStack.pop();
        window.setScene(main);
    }

    public void popupAnswerQuestion() {
        popupHelper("/fxml/popupscenes/answerQuestionPopup.fxml", 600,400);
    }

    public void popupLecturerScene() {
        popupHelper("/fxml/lecturerChatScene.fxml",1080,768);
    }

    public void popupEditQuestion() {
        popupHelper("/fxml/popupscenes/editQuestionPopup.fxml", 600,400);
    }

}
