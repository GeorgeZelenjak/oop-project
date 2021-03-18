package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Class for the Debug Scene controller.
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
                .createLecture("A great history of zebras",
                        "Andy", new Timestamp(System.currentTimeMillis()));
        Lecture.setCurrentLecture(lecture);
    }

    /**
     * Populate.
     */
    public void populate() {
        User.setUserName("Stefan");
        QuestionCommunication.askQuestion("How do you do, fellow kids?");
        QuestionCommunication.askQuestion("How do you find the eigen values?");
        QuestionCommunication.askQuestion("I am confused!!!!");
        QuestionCommunication.askQuestion("We live in a soc");
        List<Question> questions = QuestionCommunication.fetchQuestions();
        assert questions != null;
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),1);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),2);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),3);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),4);
        QuestionCommunication.upvoteQuestion(questions.get(1).getId(),5);
        QuestionCommunication.upvoteQuestion(questions.get(1).getId(),6);
    }
}
