package nl.tudelft.oopp.livechat.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.data.*;
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
    private static final long pollOptionId2 = 634526464521534536L;
    private static final long userId = 4634637365634536L;

    private static MockedStatic<AlertController> mockedAlertController;

    /**
     * A helper method to create JSON string representing a poll.
     */

    private static String createJsonPoll(String questionText, long votes, boolean isOpen) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("id", pollId);
        node.put("lectureId", lectureId.toString());
        node.put("questionText", questionText);
        node.put("time", simpleDateFormat.format(time));
        node.put("votes", votes);
        node.put("open", isOpen);
        return node.toString();
    }

    private static String createJsonPollOption(long oid, String optionText,
            long votes, boolean isCorrect) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("id", oid);
        node.put("pollId", pollId);
        node.put("optionText", optionText);
        node.put("votes", votes);
        node.put("correct", isCorrect);
        return node.toString();
    }

    private static String createJSONPollAndOptions(String pollString, String... options) {
        StringBuilder sb = new StringBuilder("{\"poll\": " + pollString + ", \"options\": [");
        for (int i = 0; i < options.length; i++) {
            sb.append(options[i]);
            if (i < options.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Create expectations for creating a poll.
     */
    private static void createExpectationsForCreatePoll() {
        String jsonPoll = createJsonPoll("Guess who's back?",401, true);

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
        String jsonPollOption = createJsonPollOption(pollOptionId,"Slim Shady", 431, true);

        //success
        mockServer.when(request().withMethod("POST").withPath("/api/poll/addOption/"
                + pollId + "/" + modkey + "/" + true).withBody("Slim Shady"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonPollOption).withHeader("Content-Type","application/json"));

        //incorrect poll id
        mockServer.when(request().withMethod("POST").withPath("/api/poll/addOption/404/"
                + lectureId + "/" + true).withBody("Slim Shady"))
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
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/toggle/404/"
                + modkey)).respond(HttpResponse.response().withStatusCode(404));

        //invalid uuid
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/toggle/"
                + pollId + "/" + invalidUUID)).respond(HttpResponse.response().withStatusCode(400));

        //incorrect modkey
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/toggle/"
                + pollId + "/" + incorrectModkey))
                .respond(HttpResponse.response().withStatusCode(401));
    }

    /**
     * Create expectations for voting for a poll option.
     */
    private static void createExpectationsForVote() {
        //success
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/vote/" + userId
                + "/" + pollOptionId)).respond(HttpResponse.response().withStatusCode(200));

        //incorrect poll option id
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/vote/" + userId
                + "/404")).respond(HttpResponse.response().withStatusCode(404));

        //incorrect user id
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/vote/404/"
                + pollOptionId)).respond(HttpResponse.response().withStatusCode(404));
    }

    /**
     * Create expectations for fetch (for students).
     */
    private static void createExpectationsForFetchStudent() {
        //success
        String pollAndOptions =
                createJSONPollAndOptions(
                        createJsonPoll("What movie has Eminem starred in?", 42, false),
                        createJsonPollOption(pollOptionId,"8 Mile", 32, true),
                        createJsonPollOption(pollOptionId2,"42 Mile", 10, false));
        mockServer.when(request().withMethod("GET").withPath("/api/poll/fetchStudent/" + lectureId))
                .respond(HttpResponse.response().withStatusCode(200).withBody(pollAndOptions)
                        .withHeader("Content-Type","application/json"));

        //lecture not found
        mockServer.when(request().withMethod("GET").withPath("/api/poll/fetchStudent/" + modkey))
                .respond(HttpResponse.response().withStatusCode(404));

        //invalid uuid
        mockServer.when(request().withMethod("GET").withPath("/api/poll/fetchStudent/"
                + invalidUUID)).respond(HttpResponse.response().withStatusCode(400));
    }

    /**
     * Create expectations for fetch (for lecturers).
     */
    private static void createExpectationsForFetchModerator() {
        //success
        String pollAndOptions =
                createJSONPollAndOptions(
                        createJsonPoll("Who is the real Slim Shady?", 69, true),
                        createJsonPollOption(pollOptionId,"Eminem", 66, true),
                        createJsonPollOption(pollOptionId2,"Dr Dre", 3, false));
        mockServer.when(request().withMethod("GET").withPath("/api/poll/fetchMod/" + lectureId
                + "/" + modkey)).respond(HttpResponse.response().withStatusCode(200)
                .withBody(pollAndOptions).withHeader("Content-Type","application/json"));

        //lecture not found
        mockServer.when(request().withMethod("GET").withPath("/api/poll/fetchMod/" + modkey
                + "/" + modkey)).respond(HttpResponse.response().withStatusCode(404));

        //invalid uuid
        mockServer.when(request().withMethod("GET").withPath("/api/poll/fetchMod/" + invalidUUID
                + "/" + modkey)).respond(HttpResponse.response().withStatusCode(400));

        //incorrect modkey
        mockServer.when(request().withMethod("GET").withPath("/api/poll/fetchMod/" + lectureId
                + "/" + incorrectModkey)).respond(HttpResponse.response().withStatusCode(401));
    }

    /**
     * Create expectations for voting for a poll option.
     */
    private static void createExpectationsForReset() {
        //success
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/reset/" + pollId + "/"
               + modkey)).respond(HttpResponse.response().withStatusCode(200));

        //incorrect poll option id
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/reset/404/" + modkey))
                .respond(HttpResponse.response().withStatusCode(404));

        //incorrect modkey
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/reset/" + pollId + "/"
                + incorrectModkey)).respond(HttpResponse.response().withStatusCode(401));

        //invalid uuid
        mockServer.when(request().withMethod("PUT").withPath("/api/poll/reset/" + pollId + "/"
                + invalidUUID)).respond(HttpResponse.response().withStatusCode(400));
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
        createExpectationsForVote();
        createExpectationsForFetchStudent();
        createExpectationsForFetchModerator();
        createExpectationsForReset();
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

    /**
     * Tests for vote.
     */
    @Test
    public void voteSuccessfulTest() {
        assertTrue(PollCommunication.vote(userId, pollOptionId));
    }

    @Test
    public void voteServerRefusesTest() {
        mockServer.stop();
        assertFalse(PollCommunication.vote(userId, pollOptionId));

        setUp();
    }

    @Test
    public void voteWrongPollOptionIdTest() {
        assertFalse(PollCommunication.vote(userId, 404));
    }

    @Test
    public void voteWrongUserIdTest() {
        assertFalse(PollCommunication.vote(404, pollOptionId));
    }

    /**
     * Tests for fetch as student.
     */
    @Test
    public void fetchPollAndOptionsStudentSuccessfulTest() {
        PollAndOptions pollAndOptions = PollCommunication.fetchPollAndOptionsStudent(lectureId);
        assertNotNull(pollAndOptions);
        assertEquals("What movie has Eminem starred in?",
                pollAndOptions.getPoll().getQuestionText());
        assertEquals(pollId, pollAndOptions.getPoll().getId());
        assertEquals(lectureId, pollAndOptions.getPoll().getLectureId());
        assertEquals(42, pollAndOptions.getPoll().getVotes());
        assertFalse(pollAndOptions.getPoll().isOpen());

        assertEquals(2, pollAndOptions.getOptions().size());

        assertEquals("8 Mile", pollAndOptions.getOptions().get(0).getOptionText());
        assertEquals(pollOptionId, pollAndOptions.getOptions().get(0).getId());
        assertEquals(32, pollAndOptions.getOptions().get(0).getVotes());
        assertTrue(pollAndOptions.getOptions().get(0).isCorrect());

        assertEquals("42 Mile", pollAndOptions.getOptions().get(1).getOptionText());
        assertEquals(pollOptionId2, pollAndOptions.getOptions().get(1).getId());
        assertEquals(10, pollAndOptions.getOptions().get(1).getVotes());
        assertFalse(pollAndOptions.getOptions().get(1).isCorrect());
    }

    @Test
    public void fetchPollAndOptionsStudentInvalidLectureIdTest() {
        assertNull(PollCommunication.fetchPollAndOptionsStudent(invalidUUID));
    }

    @Test
    public void fetchPollAndOptionsStudentLectureNotFoundTest() {
        assertNull(PollCommunication.fetchPollAndOptionsStudent(modkey));
    }

    @Test
    public void fetchPollAndOptionsStudentServerRefusesTest() {
        mockServer.stop();
        assertNull(PollCommunication.fetchPollAndOptionsStudent(lectureId));

        setUp();
    }

    /**
     * Tests for fetch as student.
     */
    @Test
    public void fetchPollAndOptionsModeratorSuccessfulTest() {
        PollAndOptions pollAndOptions =
                PollCommunication.fetchPollAndOptionsModerator(lectureId, modkey);
        assertNotNull(pollAndOptions);
        assertEquals("Who is the real Slim Shady?",
                pollAndOptions.getPoll().getQuestionText());
        assertEquals(pollId, pollAndOptions.getPoll().getId());
        assertEquals(lectureId, pollAndOptions.getPoll().getLectureId());
        assertEquals(69, pollAndOptions.getPoll().getVotes());
        assertTrue(pollAndOptions.getPoll().isOpen());

        assertEquals(2, pollAndOptions.getOptions().size());

        assertEquals("Eminem", pollAndOptions.getOptions().get(0).getOptionText());
        assertEquals(pollOptionId, pollAndOptions.getOptions().get(0).getId());
        assertEquals(66, pollAndOptions.getOptions().get(0).getVotes());
        assertTrue(pollAndOptions.getOptions().get(0).isCorrect());

        assertEquals("Dr Dre", pollAndOptions.getOptions().get(1).getOptionText());
        assertEquals(pollOptionId2, pollAndOptions.getOptions().get(1).getId());
        assertEquals(3, pollAndOptions.getOptions().get(1).getVotes());
        assertFalse(pollAndOptions.getOptions().get(1).isCorrect());
    }

    @Test
    public void fetchPollAndOptionsModeratorInvalidLectureIdTest() {
        assertNull(PollCommunication.fetchPollAndOptionsModerator(invalidUUID, modkey));
    }

    @Test
    public void fetchPollAndOptionsModeratorLectureNotFoundTest() {
        assertNull(PollCommunication.fetchPollAndOptionsModerator(modkey, modkey));
    }

    @Test
    public void fetchPollAndOptionsModeratorIncorrectModkeyTest() {
        assertNull(PollCommunication.fetchPollAndOptionsModerator(lectureId, incorrectModkey));
    }

    @Test
    public void fetchPollAndOptionsModeratorServerRefusesTest() {
        mockServer.stop();
        assertNull(PollCommunication.fetchPollAndOptionsModerator(lectureId, modkey));

        setUp();
    }

    /**
     * Tests for toggle the poll.
     */
    @Test
    public void resetVotesSuccessfulTest() {
        assertTrue(PollCommunication.resetVotes(pollId, modkey));
    }

    @Test
    public void resetVotesServerRefusesTest() {
        mockServer.stop();
        assertFalse(PollCommunication.resetVotes(pollId, modkey));

        setUp();
    }

    @Test
    public void resetVotesInvalidModkeyTest() {
        assertFalse(PollCommunication.resetVotes(pollId, invalidUUID));
    }

    @Test
    public void resetVotesWrongPollIdTest() {
        assertFalse(PollCommunication.resetVotes(404, modkey));
    }

    @Test
    public void resetVotesWrongModkeyTest() {
        assertFalse(PollCommunication.resetVotes(pollId, incorrectModkey));
    }


    @AfterAll
    public static void close() {
        mockServer.stop();
        mockedAlertController.close();
    }

}
