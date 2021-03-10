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
        goToSceneHelper("/userManual.fxml");
    }

    /**
     * Navigation to the main scene.
     *
     * @throws IOException the io exception
     */
    public void goToMainScene() throws IOException {
        goToSceneHelper("/mainScene.fxml");
    }

    /**
     * Navigation to the create room scene.
     *
     * @throws IOException the io exception
     */
    public void goToCreateRoomScene() throws IOException {
        goToSceneHelper("/inputLectureParameters.fxml");
    }

    /**
     * Navigation to the create room scene.
     *
     * @throws IOException the io exception
     */
    public void goToJoinLecturePage() throws IOException {
        goToSceneHelper("/joinLecturePage.fxml");
    }


    /**
     * Navigation to the user chat page.
     *
     * @throws IOException the io exception
     */
    public void goToUserChatPage() throws IOException {
        goToSceneHelper("/userChatPage.fxml");
    }

    private void goToSceneHelper(String javaFxFile) throws IOException {

        backStack.push(this.main);
        Parent root = FXMLLoader.load(getClass().getResource(javaFxFile));
        Stage window = (Stage) main.getWindow();
        Scene main = new Scene(root, 1000,650);
        this.main = main;
        window.setScene(main);

    }

    public void goToSettings() throws IOException {
        goToSceneHelper("/settingsPage.fxml");
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
