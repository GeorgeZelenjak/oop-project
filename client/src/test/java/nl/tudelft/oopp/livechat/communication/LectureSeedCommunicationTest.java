package nl.tudelft.oopp.livechat.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.servercommunication.LectureSpeedCommunication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;

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

    @BeforeAll
    public static void setUp() {
        mockServer = ClientAndServer.startClientAndServer(8080);
        createExpectationsForGetVotes();
    }

    @Test
    public void getVotesSuccessfulTest() {
        Lecture.setCurrentLecture(new Lecture(lid, modkey, "Lecture", "Lecturer"));
        assertEquals(List.of(2,3), LectureSpeedCommunication.getVotesOnLectureSpeed(lid));
    }

    @Test
    public void getVotesNoLectureTest() {
        Lecture.setCurrentLecture(null);
        assertNull(LectureSpeedCommunication.getVotesOnLectureSpeed(lid));
    }

    @Test
    public void getVotesServerRefusesTest() {
        stop();

        Lecture.setCurrentLecture(new Lecture(lid, modkey, "Lecture", "Lecturer"));
        assertNull(LectureSpeedCommunication.getVotesOnLectureSpeed(lid));

        setUp();
    }

    @Test
    public void getVotesInvalidIdTest() {
        Lecture.setCurrentLecture(new Lecture(lid, modkey, "Lecture", "Lecturer"));
        assertNull(LectureSpeedCommunication.getVotesOnLectureSpeed(invalidUUID));
    }

    @Test
    public void getVotesIncorrectIdTest() {
        Lecture.setCurrentLecture(new Lecture(lid, modkey, "Lecture", "Lecturer"));
        assertNull(LectureSpeedCommunication.getVotesOnLectureSpeed(wrongLid));
    }

    @AfterAll
    public static void stop() {
        mockServer.stop();
    }
}
