package nl.tudelft.oopp.livechat.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Poll;
import nl.tudelft.oopp.livechat.data.PollOption;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.PollCommunication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockserver.model.HttpRequest.request;

/**
 * Class for PollCommunication tests.
 */
public class PollCommunicationTest {
    private static MockServerClient mockServer;

    private static final UUID lectureId = UUID.randomUUID();
    private static final UUID invalidUUID = UUID.randomUUID();
    private static final UUID modkey = UUID.randomUUID();
    private static final UUID incorrectModkey = UUID.randomUUID();
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());
    private static final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    private static final long pollId = 54256564521534536L;
    private static final long pollOptionId = 1534452344521534536L;

    private static MockedStatic<AlertController> mockedAlertController;

    /*
{
    "poll": {
        "lectureId": "50325adb-25a1-486e-b2a9-2cfaa784ac49",
        "questionText": "example poll",
        "time": "2021-03-27 17:30:10 +0000",
        "votes": 0,
        "open": false,
        "id": 3046852391217721900
    },
    "options": [
        {
            "pollId": 3046852391217721900,
            "optionText": "asdasdasd",
            "votes": 0,
            "correct": false,
            "id": 3276632663248712053
        },
        {
            "pollId": 3046852391217721900,
            "optionText": "dsdaadadsasdasd",
            "votes": 0,
            "correct": false,
            "id": 5294426058538471320
        },
        {
            "pollId": 3046852391217721900,
            "optionText": "asdfasd",
            "votes": 0,
            "correct": false,
            "id": 5581020775107377740
        }
    ]
}
    * */
    /**
     * A helper method to create JSON string representing a poll.
     */

    private static String createJsonPoll(long id,
            UUID lectureId, String questionText, Timestamp time, long votes, boolean isOpen) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("id", id);
        node.put("lectureId", lectureId.toString());
        node.put("questionText", questionText);
        node.put("time", simpleDateFormat.format(time));
        node.put("votes", votes);
        node.put("isOpen", isOpen);
        return node.toString();
    }

    private static String createJsonPollOption(long id, long pollId,
            String optionText, long votes, boolean isCorrect) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("id", id);
        node.put("pollId", pollId);
        node.put("optionText", optionText);
        node.put("votes", votes);
        node.put("correct", isCorrect);
        return node.toString();
    }

    /**
     * Create expectations for creating a poll.
     */
    private static void createExpectationsForCreatePoll() {
        String jsonPoll = createJsonPoll(pollId, lectureId, "Guess who's back?",
                time, 401, true);

        //success
        mockServer.when(request().withMethod("POST").withPath("/api/poll/create/"
                + lectureId + "/" + modkey).withBody("Guess who's back?"))
                .respond(HttpResponse.response().withStatusCode(200)
                .withBody(jsonPoll).withHeader("Content-Type","application/json"));

        //invalid uuid
        mockServer.when(request().withMethod("POST").withPath("/api/poll/create/"
                + invalidUUID + "/" + modkey).withBody("Guess who's back?"))
                .respond(HttpResponse.response().withStatusCode(400));

        //incorrect modkey
        mockServer.when(request().withMethod("POST").withPath("/api/poll/create/"
                + lectureId + "/" + incorrectModkey).withBody("Guess who's back?"))
                .respond(HttpResponse.response().withStatusCode(401));
    }

    /**
     * Create expectations for creating a new poll option.
     */
    private static void createExpectationsForAddOption() {
        String jsonPollOption = createJsonPollOption(pollOptionId, pollId, "Slim Shady",
                431, true);

        //success
        mockServer.when(request().withMethod("POST").withPath("/api/poll/addOption/"
                + pollId + "/" + modkey + "/" + true).withBody("Slim Shady"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonPollOption).withHeader("Content-Type","application/json"));

        //incorrect poll id
        mockServer.when(request().withMethod("POST").withPath("/api/poll/addOption/404"
                + "/" + lectureId + "/" + true).withBody("Slim Shady"))
                .respond(HttpResponse.response().withStatusCode(404));

        //invalid uuid
        mockServer.when(request().withMethod("POST").withPath("/api/poll/addOption/"
                + pollId + "/" + invalidUUID + "/" + true).withBody("Slim Shady"))
                .respond(HttpResponse.response().withStatusCode(400));

        //incorrect modkey
        mockServer.when(request().withMethod("POST").withPath("/api/poll/addOption/"
                + pollId + "/" + incorrectModkey + "/" + true).withBody("Slim Shady"))
                .respond(HttpResponse.response().withStatusCode(401));
    }

    /**
     * Create expectations for toggling the poll state.
     */
    private static void createExpectationsForToggle() {
        //success
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/toggle/"
                + pollId + "/" + modkey)).respond(HttpResponse.response().withStatusCode(200));

        //incorrect poll id
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/toggle/404"
                + "/" + modkey)).respond(HttpResponse.response().withStatusCode(404));

        //invalid uuid
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/toggle/"
                + pollId + "/" + invalidUUID)).respond(HttpResponse.response().withStatusCode(400));

        //incorrect modkey
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/toggle/"
                + pollId + "/" + incorrectModkey))
                .respond(HttpResponse.response().withStatusCode(401));
    }

    /**
     * Set up for the tests.
     */
    @BeforeAll
    public static void setUp() {
        User.setUid();
        User.setUserName("Slim Shady");
        Lecture.setCurrentLecture(new Lecture());

        mockServer = ClientAndServer.startClientAndServer(8080);
        try {
            mockedAlertController = Mockito.mockStatic(AlertController.class);
            mockedAlertController.when(() -> AlertController.alertError(any(String.class),
                    any(String.class))).thenAnswer((Answer<Void>) invocation -> null);
        } catch (Exception e) {
            System.out.println("Caught exception!");
        }
        createExpectationsForCreatePoll();
        createExpectationsForAddOption();
        createExpectationsForToggle();
    }

    /**
     * Tests for create poll.
     */
    @Test
    public void createPollSuccessfulTest() {
        Poll poll = PollCommunication.createPoll(lectureId, modkey, "Guess who's back?");
        assertNotNull(poll);

        assertEquals(pollId, poll.getId());
        assertEquals("Guess who's back?", poll.getQuestionText());
    }

    @Test
    public void createPollEmptyTextTest() {
        assertNull(PollCommunication.createPoll(lectureId, modkey, ""));
    }

    @Test
    public void createPollNullTextTest() {
        assertNull(PollCommunication.createPoll(lectureId, modkey, null));
    }

    @Test
    public void createPollServerRefusesTest() {
        mockServer.stop();
        assertNull(PollCommunication.createPoll(lectureId, modkey, "Guess who's back?"));

        setUp();
    }

    @Test
    public void createPollInvalidLectureIdTest() {
        assertNull(PollCommunication.createPoll(invalidUUID, modkey, "Guess who's back?"));
    }

    @Test
    public void createPollWrongModkeyTest() {
        assertNull(PollCommunication.createPoll(lectureId, incorrectModkey, "Guess who's back?"));
    }

    /**
     * Tests for add option.
     */
    @Test
    public void addOptionSuccessfulTest() {
        PollOption pollOption =
                PollCommunication.addOption(pollId, modkey, true, "Slim Shady");
        assertNotNull(pollOption);

        assertEquals(pollOptionId, pollOption.getId());
        assertEquals(pollId, pollOption.getPollId());
        assertEquals("Slim Shady", pollOption.getOptionText());
        assertTrue(pollOption.isCorrect());
    }

    @Test
    public void addOptionEmptyTextTest() {
        assertNull(PollCommunication.addOption(pollId, modkey, true, ""));
    }

    @Test
    public void addOptionNullTextTest() {
        assertNull(PollCommunication.addOption(pollId, modkey, true, null));
    }

    @Test
    public void addOptionServerRefusesTest() {
        mockServer.stop();
        assertNull(PollCommunication.addOption(pollId, modkey, true, "Slim Shady"));

        setUp();
    }

    @Test
    public void addOptionInvalidModkeyTest() {
        assertNull(PollCommunication.addOption(pollId,
                invalidUUID, true, "Slim Shady"));
    }

    @Test
    public void addOptionWrongPollIdTest() {
        assertNull(PollCommunication.addOption(404,
                modkey, true, "Slim Shady"));
    }

    @Test
    public void addOptionWrongModkeyTest() {
        assertNull(PollCommunication.addOption(pollId,
                incorrectModkey, true, "Slim Shady"));
    }

    /**
     * Tests for toggle the poll.
     */
    @Test
    public void toggleSuccessfulTest() {
        assertTrue(PollCommunication.toggle(pollId, modkey));
    }

    @Test
    public void toggleServerRefusesTest() {
        mockServer.stop();
        assertFalse(PollCommunication.toggle(pollId, modkey));

        setUp();
    }

    @Test
    public void toggleInvalidModkeyTest() {
        assertFalse(PollCommunication.toggle(pollId, invalidUUID));
    }

    @Test
    public void toggleWrongPollIdTest() {
        assertFalse(PollCommunication.toggle(404, modkey));
    }

    @Test
    public void toggleWrongModkeyTest() {
        assertFalse(PollCommunication.toggle(pollId, incorrectModkey));
    }

    @AfterAll
    public static void close() {
        mockServer.stop();
        mockedAlertController.close();
    }

}
