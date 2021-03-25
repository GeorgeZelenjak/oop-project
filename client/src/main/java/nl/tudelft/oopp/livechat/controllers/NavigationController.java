package nl.tudelft.oopp.livechat.controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.tudelft.oopp.livechat.controllers.popupcontrollers.PollingManagementPopupController;

import javax.sql.PooledConnection;
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
        goToSceneHelper("/fxml/scenes/userManualScene.fxml");
    }

    /**
     * Navigation to the main scene.
     */
    public void goToMainScene()  {
        goToSceneHelper("/fxml/scenes/mainScene.fxml");
    }

    /**
     * Navigation to the create room scene.
     */
    public void goToCreateRoomScene()  {
        goToSceneHelper("/fxml/scenes/createLectureScene.fxml");
    }

    /**
     * Navigation to the create room scene.
     */
    public void goToJoinLecturePage()  {
        goToSceneHelper("/fxml/scenes/joinLectureScene.fxml");
    }

    /**
     * Navigation to the lecturer chat room scene.
     */
    public void goToLecturerChatPage() {
        goToSceneHelper("/fxml/scenes/lecturerChatScene.fxml");
    }

    /**
     * Navigation to the user chat page.
     */
    public void goToUserChatPage() {
        goToSceneHelper("/fxml/scenes/userChatScene.fxml");
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

            //Closes the entire program when the main scene is closed
            window.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    Platform.exit();
                }
            });

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
        goToSceneHelper("/fxml/scenes/settingsScene.fxml");
    }

    /**
     * Go to test scene.
     */
    @SuppressWarnings("unused")
    public void goToTestScene() {
        goToSceneHelper("/fxml/scenes/newUserChatScene.fxml");
    }

    public void goToDebugScene() {
        goToSceneHelper("/fxml/scenes/debugScene.fxml");
    }

    /**
     * Navigates to the previous scene.
     */
    public void goBack() {
        Stage window = (Stage) main.getWindow();
        this.main = backStack.pop();
        window.setScene(main);
    }

    /**
     * Makes a popup to answer question.
     */
    public void popupAnswerQuestion() {
        popupHelper("/fxml/popupscenes/answerQuestionPopup.fxml", 600,400);
    }

    /**
     * Makes a popup to the lecturer scene.
     */
    public void popupLecturerScene() {
        popupHelper("/fxml/scenes/lecturerChatScene.fxml",1080,768);
    }

    /**
     * Makes a popup to edit question.
     */
    public void popupEditQuestion() {
        popupHelper("/fxml/popupscenes/editQuestionPopup.fxml", 600,400);
    }

    /**
     * Popup polling management.
     */
    public void popupPollingManagement() {
        popupHelper("/fxml/popupscenes/pollingManagementPopup.fxml", 720, 512);
    }
}
