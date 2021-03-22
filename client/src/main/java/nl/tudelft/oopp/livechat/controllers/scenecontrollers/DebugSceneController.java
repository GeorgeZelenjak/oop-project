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
        User.setUserName("Andy");
        Lecture lecture = LectureCommunication
                .createLecture("A great history of zebras",
                        "Andy", new Timestamp(System.currentTimeMillis()));
        Lecture.setCurrentLecture(lecture);
    }

    public void joinAsBoth() throws IOException {
        NavigationController.getCurrentController().goToUserChatPage();
        NavigationController.getCurrentController().popupLecturerScene();
    }

    /**
     * Populate.
     */
    public void populate() {
        User.setUserName("Stefan");
        List<Question> questions = QuestionCommunication.fetchQuestions();
        assert questions != null;
        assert questions.size() == 4;
        LectureCommunication.registerUserdebug(Lecture.getCurrentLecture().getUuid().toString(),
                67, "Artjom");

        LectureCommunication.registerUserdebug(Lecture.getCurrentLecture().getUuid().toString(),
                26, "Codrin");

        LectureCommunication.registerUserdebug(Lecture.getCurrentLecture().getUuid().toString(),
                34, "Jegor");

        LectureCommunication.registerUserdebug(Lecture.getCurrentLecture().getUuid().toString(),
                42, "Tudor");

        LectureCommunication.registerUserdebug(Lecture.getCurrentLecture().getUuid().toString(),
                59, "Oleg");
        QuestionCommunication.askQuestion(
                67, Lecture.getCurrentLecture().getUuid(), "How do you do, fellow kids?");
        QuestionCommunication.askQuestion(
                26, Lecture.getCurrentLecture().getUuid(),"How do you find the eigen values?");
        QuestionCommunication.askQuestion(
                26, Lecture.getCurrentLecture().getUuid(),"I am confused!!!!");
        QuestionCommunication.askQuestion(
                42, Lecture.getCurrentLecture().getUuid(),"We live in a soc");
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),67);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),26);
        QuestionCommunication.upvoteQuestion(questions.get(2).getId(),34);
        QuestionCommunication.upvoteQuestion(questions.get(1).getId(),42);
        QuestionCommunication.upvoteQuestion(questions.get(1).getId(),59);



        LectureSpeedCommunication.voteOnLectureSpeed(
                67,
                Lecture.getCurrentLecture().getUuid(),
                "faster");
        LectureSpeedCommunication.voteOnLectureSpeed(
                26,
                Lecture.getCurrentLecture().getUuid(),
                "faster");
        LectureSpeedCommunication.voteOnLectureSpeed(
                34,
                Lecture.getCurrentLecture().getUuid(),
                "faster");
        LectureSpeedCommunication.voteOnLectureSpeed(
                42,
                Lecture.getCurrentLecture().getUuid(),
                "slower");
        LectureSpeedCommunication.voteOnLectureSpeed(
                59,
                Lecture.getCurrentLecture().getUuid(),
                "faster");
    }
}
