package nl.tudelft.oopp.livechat.communication;

import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureSpeedCommunication;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockserver.model.HttpRequest.request;

public class LectureSpeedCommunicationTest {
    public static MockServerClient mockServer;

    private static final UUID lid = UUID.randomUUID();
    private static final UUID wrongLid = UUID.randomUUID();
    private static final UUID invalidUUID = UUID.randomUUID();
    private static final UUID modkey = UUID.randomUUID();
    private static final UUID incorrectModkey = UUID.randomUUID();
    private static long userId;

    private static MockedStatic<AlertController> alertControllerMockedStatic;


    /**
     * Create expectations for getting votes on the lecture speed.
     */
    private static void createExpectationsForGetVotes() {
        //Success
        mockServer.when(request().withMethod("GET")
                .withPath("/api/vote/getLectureSpeed/" + lid))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("[2,3]")
                        .withHeader("Content-Type","application/json"));

        //Invalid request
        mockServer.when(request().withMethod("GET")
                .withPath("/api/vote/getLectureSpeed/" + invalidUUID))
                .respond(HttpResponse.response().withStatusCode(400));

        //Lecture not found
        mockServer.when(request().withMethod("GET")
                .withPath("/api/vote/getLectureSpeed/" + wrongLid))
                .respond(HttpResponse.response().withStatusCode(404)
                        .withBody("")
                        .withHeader("Content-Type","application/json"));
    }

    /**
     * Create expectations for voting on the lecture speed.
     */
    private static void createExpectationsForVoting() {
        //Success faster
        mockServer.when(request().withMethod("PUT").withPath("/api/vote/lectureSpeed")
                .withQueryStringParameters(new Parameter("uid", "" + userId),
                        new Parameter("uuid", lid.toString())).withBody("faster"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //Success slower
        mockServer.when(request().withMethod("PUT").withPath("/api/vote/lectureSpeed")
                .withQueryStringParameters(new Parameter("uid", "" + userId),
                        new Parameter("uuid", lid.toString())).withBody("slower"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //Invalid request
        mockServer.when(request().withMethod("PUT").withPath("/api/vote/lectureSpeed")
                .withQueryStringParameters(new Parameter("uid", "" + userId),
                        new Parameter("uuid", invalidUUID.toString())).withBody("faster"))
                .respond(HttpResponse.response().withStatusCode(400));

        //Invalid speed
        mockServer.when(request().withMethod("PUT").withPath("/api/vote/lectureSpeed")
                .withQueryStringParameters(new Parameter("uid", "" + userId),
                        new Parameter("uuid", lid.toString())).withBody("a lot faster"))
                .respond(HttpResponse.response().withStatusCode(400)
                        .withBody("-1").withHeader("Content-Type","application/json"));

        //Lecture not found
        mockServer.when(request().withMethod("PUT").withPath("/api/vote/lectureSpeed")
                .withQueryStringParameters(new Parameter("uid", "" + userId),
                        new Parameter("uuid", wrongLid.toString())).withBody("slower"))
                .respond(HttpResponse.response().withStatusCode(404)
                        .withBody("-1").withHeader("Content-Type","application/json"));
    }

    /**
     * Create expectations for resetting vote on the lecture speed.
     */
    private static void createExpectationsForResetting() {
        //Success
        mockServer.when(request().withMethod("DELETE")
                .withPath("/api/vote/resetLectureSpeedVote/" + lid + "/" + modkey))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type", "application/json"));

        //Invalid request
        mockServer.when(request().withMethod("DELETE")
                .withPath("/api/vote/resetLectureSpeedVote/" + invalidUUID + "/" + modkey))
                .respond(HttpResponse.response().withStatusCode(400));

        //Lecture not found
        mockServer.when(request().withMethod("DELETE")
                .withPath("/api/vote/resetLectureSpeedVote/" + wrongLid + "/" + modkey))
                .respond(HttpResponse.response().withStatusCode(404)
                        .withBody("-1").withHeader("Content-Type", "application/json"));

        //Incorrect modkey
        mockServer.when(request().withMethod("DELETE")
                .withPath("/api/vote/resetLectureSpeedVote/" + lid + "/" + incorrectModkey))
                .respond(HttpResponse.response().withStatusCode(400)
                        .withBody("-1").withHeader("Content-Type", "application/json"));
    }

    /**
     * Setup for the tests.
     */
    @BeforeAll
    public static void setUp() {
        mockServer = ClientAndServer.startClientAndServer(8080);
        User.setUid();
        userId = User.getUid();
        Lecture.setCurrent(new Lecture(lid, modkey, "Lecture", "Lecturer"));

        createExpectationsForGetVotes();
        createExpectationsForVoting();
        createExpectationsForResetting();
        try {
            alertControllerMockedStatic = Mockito.mockStatic(AlertController.class);
            alertControllerMockedStatic.when(() -> AlertController.alertError(any(String.class),
                    any(String.class))).thenAnswer((Answer<Void>) invocation -> null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests for get votes.
     */
    @Test
    public void getVotesSuccessfulTest() {
        assertEquals(List.of(2,3), LectureSpeedCommunication.getVotesOnLectureSpeed(lid));
    }

    @Test
    public void getVotesNoLectureTest() {
        Lecture.setCurrent(null);
        assertNull(LectureSpeedCommunication.getVotesOnLectureSpeed(lid));

        Lecture.setCurrent(new Lecture(lid, modkey, "Lecture", "Lecturer"));
    }

    @Test
    public void getVotesServerRefusesTest() {
        mockServer.stop();
        assertNull(LectureSpeedCommunication.getVotesOnLectureSpeed(lid));

        setUp();
    }

    @Test
    public void getVotesInvalidIdTest() {
        assertNull(LectureSpeedCommunication.getVotesOnLectureSpeed(invalidUUID));
    }

    @Test
    public void getVotesIncorrectIdTest() {
        assertNull(LectureSpeedCommunication.getVotesOnLectureSpeed(wrongLid));
    }

    /**
     * Tests for vote.
     */
    @Test
    public void voteSuccessfulFasterTest() {
        assertTrue(LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "faster"));
    }

    @Test
    public void voteSuccessfulSlowerTest() {
        assertTrue(LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "slower"));
    }

    @Test
    public void voteSuccessfulBothTest() {
        assertTrue(LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "slower"));
        assertTrue(LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "faster"));
    }

    @Test
    public void voteNoLectureTest() {
        Lecture.setCurrent(null);
        assertFalse(LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "faster"));

        Lecture.setCurrent(new Lecture(lid, modkey, "Lecture", "Lecturer"));
    }

    @Test
    public void voteServerRefusesTest() {
        mockServer.stop();
        assertFalse(LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "faster"));

        setUp();
    }

    @Test
    public void voteWrongSpeedTest() {
        assertFalse(LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "a lot faster"));
    }

    @Test
    public void voteInvalidIdTest() {
        assertFalse(LectureSpeedCommunication.voteOnLectureSpeed(userId, invalidUUID, "faster"));
    }

    @Test
    public void voteIncorrectIdTest() {
        assertFalse(LectureSpeedCommunication.voteOnLectureSpeed(userId, wrongLid, "slower"));
    }

    /**
     * Tests for resetting.
     */
    @Test
    public void resetSuccessfulTest() {
        assertTrue(LectureSpeedCommunication.resetLectureSpeed(lid, modkey));
    }

    @Test
    public void resetNoLectureTest() {
        Lecture.setCurrent(null);
        assertFalse(LectureSpeedCommunication.resetLectureSpeed(lid, modkey));

        Lecture.setCurrent(new Lecture(lid, modkey, "Lecture", "Lecturer"));
    }

    @Test
    public void resetServerRefusesTest() {
        mockServer.stop();
        assertFalse(LectureSpeedCommunication.resetLectureSpeed(lid, modkey));

        setUp();
    }

    @Test
    public void resetInvalidIdTest() {
        assertFalse(LectureSpeedCommunication.resetLectureSpeed(invalidUUID, modkey));
    }

    @Test
    public void resetIncorrectIdTest() {
        assertFalse(LectureSpeedCommunication.resetLectureSpeed(wrongLid, modkey));
    }

    @Test
    public void resetIncorrectModkeyTest() {
        assertFalse(LectureSpeedCommunication.resetLectureSpeed(lid, incorrectModkey));
    }

    @AfterAll
    public static void stop() {
        mockServer.stop();
        alertControllerMockedStatic.close();
    }
}
