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

    private Stack<Scene> backStack;


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
     *
     * @throws IOException the io exception
     */
    public void goToUserManual() throws IOException {
        goToSceneHelper("/fxml/userManualScene.fxml");
    }

    /**
     * Navigation to the main scene.
     *
     * @throws IOException the io exception
     */
    public void goToMainScene() throws IOException {
        goToSceneHelper("/fxml/mainScene.fxml");
    }

    /**
     * Navigation to the create room scene.
     *
     * @throws IOException the io exception
     */
    public void goToCreateRoomScene() throws IOException {
        goToSceneHelper("/fxml/createLectureScene.fxml");
    }

    /**
     * Navigation to the create room scene.
     *
     * @throws IOException the io exception
     */
    public void goToJoinLecturePage() throws IOException {
        goToSceneHelper("/fxml/joinLectureScene.fxml");
    }

    /**
     * Navigation to the lecturer chat room scene.
     *
     * @throws IOException the io exception
     */
    public void goToLecturerChatPage() throws IOException {
        goToSceneHelper("/fxml/lecturerChatScene.fxml");
    }

    /**
     * Navigation to the user chat page.
     *
     * @throws IOException the io exception
     */
    public void goToUserChatPage() throws IOException {
        goToSceneHelper("/fxml/userChatScene.fxml");
    }

    private void goToSceneHelper(String javaFxFile) throws IOException {

        backStack.push(this.main);
        Parent root = FXMLLoader.load(getClass().getResource(javaFxFile));
        Stage window = (Stage) main.getWindow();
        Scene main = new Scene(root, 1080,768);
        this.main = main;
        window.setScene(main);

    }

    public void goToSettings() throws IOException {
        goToSceneHelper("/fxml/settingsScene.fxml");
    }

    /**
     * Navigates to the previous scene.
     */
    public void goBack() {

        Stage window = (Stage) main.getWindow();
        this.main = backStack.pop();
        window.setScene(main);

    }

}
