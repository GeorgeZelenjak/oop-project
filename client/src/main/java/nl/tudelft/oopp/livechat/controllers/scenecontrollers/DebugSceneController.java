package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import nl.tudelft.oopp.livechat.controllers.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import nl.tudelft.oopp.livechat.servercommunication.LectureSpeedCommunication;
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
        NavigationController.getCurrent().goToUserChatPage();
    }

    /**
     * Join lecture as a moderator.
     *
     * @throws IOException exception if something goes wrong
     */
    public void joinAsModerator() throws IOException {
        NavigationController.getCurrent().goToLecturerChatPage();
    }

    /**
     * Go to settings.
     *
     * @throws IOException the io exception
     */
    public void goToSettings() throws IOException {
        NavigationController.getCurrent().goToSettings();
    }

    /**
     * Go back.
     */
    public void goBack() {
        NavigationController.getCurrent().goBack();
    }

    /**
     * Sets new lecture.
     */
    public void setNewLecture() {
        User.setUserName("Andy");
        Lecture lecture = LectureCommunication
                .createLecture("A great history of zebras",
                        "Andy", new Timestamp(System.currentTimeMillis()), 60);
        Lecture.setCurrent(lecture);
    }

    public void joinAsBoth() throws IOException {
        NavigationController.getCurrent().goToUserChatPage();
        NavigationController.getCurrent().popupLecturerScene();
    }

    /**
     * Populate.
     */
    public void populate() {
        User.setUserName("Stefan");
        LectureCommunication.registerUserdebug(Lecture.getCurrent().getUuid().toString(),
                67, "Artjom");

        LectureCommunication.registerUserdebug(Lecture.getCurrent().getUuid().toString(),
                26, "Codrin");

        LectureCommunication.registerUserdebug(Lecture.getCurrent().getUuid().toString(),
                34, "Jegor");

        LectureCommunication.registerUserdebug(Lecture.getCurrent().getUuid().toString(),
                42, "Tudor");

        LectureCommunication.registerUserdebug(Lecture.getCurrent().getUuid().toString(),
                59, "Oleg");

        QuestionCommunication.askQuestion(
                67, Lecture.getCurrent().getUuid(), "How do you do, fellow kids?");
        QuestionCommunication.askQuestion(
                59, Lecture.getCurrent().getUuid(),"How do you find the eigen values?");
        QuestionCommunication.askQuestion(
                26, Lecture.getCurrent().getUuid(),"I am confused!!!!");
        QuestionCommunication.askQuestion(
                42, Lecture.getCurrent().getUuid(),"We live in a soc");
        List<Question> questions = QuestionCommunication.fetchQuestions(true);
        assert questions != null;
        assert questions.size() == 4;
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),67);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),26);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),34);
        QuestionCommunication.upvoteQuestion(questions.get(1).getId(),42);
        QuestionCommunication.upvoteQuestion(questions.get(1).getId(),59);



        LectureSpeedCommunication.voteOnLectureSpeed(
                67,
                Lecture.getCurrent().getUuid(),
                "faster");
        LectureSpeedCommunication.voteOnLectureSpeed(
                26,
                Lecture.getCurrent().getUuid(),
                "faster");
        LectureSpeedCommunication.voteOnLectureSpeed(
                34,
                Lecture.getCurrent().getUuid(),
                "faster");
        LectureSpeedCommunication.voteOnLectureSpeed(
                42,
                Lecture.getCurrent().getUuid(),
                "slower");
        LectureSpeedCommunication.voteOnLectureSpeed(
                59,
                Lecture.getCurrent().getUuid(),
                "faster");
    }
}
