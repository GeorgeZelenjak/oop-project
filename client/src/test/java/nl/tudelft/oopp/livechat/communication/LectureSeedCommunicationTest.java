package nl.tudelft.oopp.livechat.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureSpeedCommunication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;

public class LectureSeedCommunicationTest {
    public static MockServerClient mockServer;

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
            .excludeFieldsWithoutExposeAnnotation().create();

    private static final UUID lid = UUID.randomUUID();
    private static final UUID wrongLid = UUID.randomUUID();
    private static final UUID invalidUUID = UUID.randomUUID();
    private static final UUID modkey = UUID.randomUUID();
    private static final UUID incorrectModkey = UUID.randomUUID();
    private static long userId;

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
                .respond(HttpResponse.response().withStatusCode(200)
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
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1").withHeader("Content-Type","application/json"));

        //Lecture not found
        mockServer.when(request().withMethod("PUT").withPath("/api/vote/lectureSpeed")
                .withQueryStringParameters(new Parameter("uid", "" + userId),
                        new Parameter("uuid", wrongLid.toString())).withBody("slower"))
                .respond(HttpResponse.response().withStatusCode(200)
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
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1").withHeader("Content-Type", "application/json"));

        //Incorrect modkey
        mockServer.when(request().withMethod("DELETE")
                .withPath("/api/vote/resetLectureSpeedVote/" + lid + "/" + incorrectModkey))
                .respond(HttpResponse.response().withStatusCode(200)
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
        Lecture.setCurrentLecture(new Lecture(lid, modkey, "Lecture", "Lecturer"));

        createExpectationsForGetVotes();
        createExpectationsForVoting();
        createExpectationsForResetting();
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
        Lecture.setCurrentLecture(null);
        assertNull(LectureSpeedCommunication.getVotesOnLectureSpeed(lid));

        Lecture.setCurrentLecture(new Lecture(lid, modkey, "Lecture", "Lecturer"));
    }

    @Test
    public void getVotesServerRefusesTest() {
        stop();
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
        assertEquals(0, LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "faster"));
    }

    @Test
    public void voteSuccessfulSlowerTest() {
        assertEquals(0, LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "slower"));
    }

    @Test
    public void voteSuccessfulBothTest() {
        assertEquals(0, LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "slower"));
        assertEquals(0, LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "faster"));
    }

    @Test
    public void voteNoLectureTest() {
        Lecture.setCurrentLecture(null);
        assertEquals(-1, LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "faster"));

        Lecture.setCurrentLecture(new Lecture(lid, modkey, "Lecture", "Lecturer"));
    }

    @Test
    public void voteServerRefusesTest() {
        stop();
        assertEquals(-2, LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "faster"));
        setUp();
    }

    @Test
    public void voteWrongSpeedTest() {
        assertEquals(-4, LectureSpeedCommunication.voteOnLectureSpeed(userId, lid, "a lot faster"));
    }

    @Test
    public void voteInvalidIdTest() {
        assertEquals(-3, LectureSpeedCommunication.voteOnLectureSpeed(userId, invalidUUID, "faster"));
    }

    @Test
    public void voteIncorrectIdTest() {
        assertEquals(-4, LectureSpeedCommunication.voteOnLectureSpeed(userId, wrongLid, "slower"));
    }

    /**
     * Tests for resetting.
     */
    @Test
    public void resetSuccessfulTest() {
        assertEquals(0, LectureSpeedCommunication.resetLectureSpeed(lid, modkey));
    }

    @Test
    public void resetNoLectureTest() {
        Lecture.setCurrentLecture(null);
        assertEquals(-1, LectureSpeedCommunication.resetLectureSpeed(lid, modkey));

        Lecture.setCurrentLecture(new Lecture(lid, modkey, "Lecture", "Lecturer"));
    }

    @Test
    public void resetServerRefusesTest() {
        stop();
        assertEquals(-2, LectureSpeedCommunication.resetLectureSpeed(lid, modkey));
        setUp();
    }

    @Test
    public void resetInvalidIdTest() {
        assertEquals(-3, LectureSpeedCommunication.resetLectureSpeed(invalidUUID, modkey));
    }

    @Test
    public void resetIncorrectIdTest() {
        assertEquals(-4, LectureSpeedCommunication.resetLectureSpeed(wrongLid, modkey));
    }

    @Test
    public void resetIncorrectModkeyTest() {
        assertEquals(-4, LectureSpeedCommunication.resetLectureSpeed(lid, incorrectModkey));
    }

    @AfterAll
    public static void stop() {
        mockServer.stop();
    }
}
