package nl.tudelft.oopp.livechat.controllers.scenecontrollers;

import nl.tudelft.oopp.livechat.controllers.gui.NavigationController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import nl.tudelft.oopp.livechat.servercommunication.LectureSpeedCommunication;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;


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

    }
}
