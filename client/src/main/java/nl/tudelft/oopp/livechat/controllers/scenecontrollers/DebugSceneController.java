package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.io.IOException;
import java.util.List;

/**
 * The type Debug scene controller.
 */
public class DebugSceneController {

    /**
     * Join lecture as a student.
     *
     * @throws IOException exception if something goes wrong
     */
    public void joinAsStudent() throws IOException {
        NavigationController.getCurrentController().goToUserChatPage();
    }

    /**
     * Join lecture as a moderator.
     *
     * @throws IOException exception if something goes wrong
     */
    public void joinAsModerator() throws IOException {
        NavigationController.getCurrentController().goToLecturerChatPage();
    }

    /**
     * Go to settings.
     *
     * @throws IOException the io exception
     */
    public void goToSettings() throws IOException {
        NavigationController.getCurrentController().goToSettings();
    }

    /**
     * Go back.
     */
    public void goBack() {
        NavigationController.getCurrentController().goBack();
    }

    /**
     * Sets new lecture.
     */
    public void setNewLecture() {
        Lecture lecture = LectureCommunication
                .createLecture("A great history of zebras");
        Lecture.setCurrentLecture(lecture);
    }

    /**
     * Populate.
     */
    public void populate() {

        QuestionCommunication.askQuestion("How do you do, fellow kids?");
        QuestionCommunication.askQuestion("How do you find the eigen values?");
        QuestionCommunication.askQuestion("I am confused!!!!");
        List<Question> questions = QuestionCommunication.fetchQuestions();
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),1);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),2);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),3);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),4);
        QuestionCommunication.upvoteQuestion(questions.get(1).getId(),5);
        QuestionCommunication.upvoteQuestion(questions.get(1).getId(),6);
    }
}
