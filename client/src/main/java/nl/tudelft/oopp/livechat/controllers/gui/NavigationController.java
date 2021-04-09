package nl.tudelft.oopp.livechat.controllers.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

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
    public static void setCurrent(NavigationController currentController) {
        NavigationController.currentController = currentController;
    }

    /**
     * Gets current controller.
     *
     * @return the current controller
     */
    public static NavigationController getCurrent() {
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
            double width = this.main.getWidth();
            double height = this.main.getHeight();
            Scene main = new Scene(root, width, height);
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
            return;
        }
    }

    private void popupHelper(String javaFxFile, int width, int height, String title) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource(javaFxFile));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();

        } catch (IOException e) {
            return;
        }
    }

    private void popupHelperSendRequests(String javaFxFile, int width, int height,
                                         String req, String title) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource(javaFxFile));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            if (!QuestionCommunication.setStatus(Question.getCurrent().getId(),
                    Lecture.getCurrent().getModkey(), req, User.getUid())) {
                AlertController.alertWarning("Question is already being handled",
                        "This question is already being handled, if you want you can continue");
            }
            stage.show();
            //Closes the entire program when the main scene is closed
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    QuestionCommunication.setStatus(Question.getCurrent().getId(),
                            Lecture.getCurrent().getModkey(), "new", User.getUid());
                }
            });
        } catch (IOException e) {
            return;
        }

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
        double remWidth = window.getWidth();
        double remHeight = window.getHeight();
        this.main = backStack.pop();
        window.setWidth(remWidth);
        window.setHeight(remHeight);
        window.setScene(main);
    }

    /**
     * Makes a popup to answer question.
     */
    public void popupAnswerQuestion() {
        popupHelperSendRequests("/fxml/popupscenes/answerQuestionPopup.fxml", 600,400,
                "answering","Answer");
    }

    /**
     * Makes a popup to the lecturer scene.
     */
    public void popupLecturerScene() {
        popupHelper("/fxml/scenes/lecturerChatScene.fxml",1080,768,
                "debug");
    }

    /**
     * Makes a popup to edit question.
     */
    public void popupEditQuestion() {
        popupHelperSendRequests("/fxml/popupscenes/editQuestionPopup.fxml", 600,400,
                "editing", "Edit");
    }

    /**
     * Popup polling management.
     */
    public void popupPollingManagement() {
        popupHelper("/fxml/popupscenes/pollingManagementPopup.fxml", 720, 512,
                "Polls and Quizzes");
    }

    /**
     * Popup poll result.
     */
    public void popupPollResult() {
        popupHelper("/fxml/popupscenes/pollResultsPopup.fxml",
                720, 512, "Results");
    }

    /**
     * Popup poll voting.
     */
    public void popupPollVoting() {
        popupHelper("/fxml/popupscenes/pollVotingPopup.fxml",
                720, 512, "Vote");
    }
}
