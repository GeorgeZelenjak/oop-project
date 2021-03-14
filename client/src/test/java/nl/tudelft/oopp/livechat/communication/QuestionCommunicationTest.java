package nl.tudelft.oopp.livechat.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;

/**
 * Class for Question communication tests.
 */
public class QuestionCommunicationTest {

    public static MockServerClient mockServer;

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
            .excludeFieldsWithoutExposeAnnotation().create();

    private static final UUID lid = UUID.fromString("dfabcfdf-271b-48d2-841e-4874ff28b4a6");
    private static final UUID modkey = UUID.randomUUID();
    private static final UUID incorrectModkey = UUID.randomUUID();
    private static final String qid1 = "5397545054934456486";
    private static final String qid2 = "8077505054105457480";

    private static final String goodQuestion = gson.toJson(
                                new Question(lid, "Is there anybody?",  42));
    private static final String badQuestion = gson.toJson(
                                new Question(lid, "F*ck",  69));

    private static final String response =  "[\n"
            + "    {\n"
            + "        \"id\": " + qid1 + ",\n"
            + "        \"lectureId\": \"" + lid + "\",\n"
            + "        \"time\": \"2021-03-11T12:37:37.403+0000\",\n"
            + "        \"votes\": 0,\n"
            + "        \"text\": \"HHH\",\n"
            + "        \"answered\": false,\n"
            + "        \"answerText\": null,\n"
            + "        \"answerTime\": null\n"
            + "    },\n"
            + "    {\n"
            + "        \"id\": " + qid2 + ",\n"
            + "        \"lectureId\": \"" + lid + "\",\n"
            + "        \"time\": \"2021-03-11T12:37:41.344+0000\",\n"
            + "        \"votes\": 0,\n"
            + "        \"text\": \"koiko\",\n"
            + "        \"answered\": false,\n"
            + "        \"answerText\": null,\n"
            + "        \"answerTime\": null\n"
            + "    }\n"
            + "]";

    /**
     * Create expectations for asking question.
     */
    private static void createExpectationsForAsking() {
        //Success
        mockServer.when(request().withMethod("POST").withPath("/api/question/ask")
                    .withBody(goodQuestion))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(qid1)
                        .withHeader("Content-Type","application/json"));

        //Invalid request - (treat a curse word as invalid question to test BAD REQUEST)
        mockServer.when(request().withMethod("POST").withPath("/api/question/ask")
                    .withBody(badQuestion))
                .respond(HttpResponse.response().withStatusCode(400));
    }

    /**
     * Create expectations for fetching questions.
     */
    private static void createExpectationsForFetching() {
        //Success
        mockServer.when(request().withMethod("GET").withPath("/api/question/fetch")
                .withQueryStringParameter("lid", lid.toString()))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(response)
                        .withHeader("Content-Type","application/json"));

        //No questions found
        mockServer.when(request().withMethod("GET").withPath("/api/question/fetch")
                .withQueryStringParameter("lid", modkey.toString()))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("[]")
                        .withHeader("Content-Type","application/json"));

        //Invalid lecture id - send 400
        //  (treat "incorrectModkey" as invalid UUID to test BAD REQUEST)
        mockServer.when(request().withMethod("GET").withPath("/api/question/fetch")
                .withQueryStringParameter("lid", incorrectModkey.toString()))
                .respond(HttpResponse.response().withStatusCode(400));
    }

    /**
     * Create expectations for upvoting a question.
     */
    private static void createExpectationsForUpvote() {
        //Success
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", qid1),
                        new Parameter("uid", "443")))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //invalid parameter - send 400
        // (treat Long.MAX_VALUE as invalid parameter to test BAD REQUEST)
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", qid1),
                        new Parameter("uid", String.valueOf(Long.MAX_VALUE))))
                .respond(HttpResponse.response().withStatusCode(400));

        //uid not found
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", qid1),
                        new Parameter("uid", "442")))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1").withHeader("Content-Type","application/json"));

        //qid not found
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "666"),
                        new Parameter("uid", "443")))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1").withHeader("Content-Type","application/json"));
    }

    /**
     * Create expectations for marking question as answered.
     */
    private static void createExpectationsForMarkAsAnswered() {
        //Success
        mockServer.when(request().withMethod("PUT").withPath("/api/question/answer/"
                        + qid1 + "/" +  modkey))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //invalid parameter - send 400
        //  (treat lecture id (lid) as invalid UUID here to test BAD REQUEST)
        mockServer.when(request().withMethod("PUT").withPath("/api/question/answer/"
                        + qid1 + "/" +  lid))
                .respond(HttpResponse.response().withStatusCode(400));

        //qid not found
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/question/answer/" + 666 + "/" +  modkey))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1").withHeader("Content-Type","application/json"));

        //incorrect modkey
        mockServer.when(request().withMethod("PUT").withPath("/api/question/answer/"
                + qid1 + "/" +  incorrectModkey))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1").withHeader("Content-Type","application/json"));
    }

    /**
     * Starts mock server.
     */
    @BeforeAll
    public static void startServer() {
        mockServer = ClientAndServer.startClientAndServer(8080);

        createExpectationsForAsking();
        createExpectationsForFetching();
        createExpectationsForUpvote();
        createExpectationsForMarkAsAnswered();
    }


    /**
     * Tests for asking.
     */

    @Test
    public void askQuestionSuccessfulTest() {
        Lecture.setCurrentLecture(new Lecture(lid, modkey, "Testing", "Andy"));
        assertEquals(0, QuestionCommunication.askQuestion("Is there anybody?"));
    }

    @Test
    public void askQuestionLectureNotExistsTest() {
        Lecture.setCurrentLecture(null);
        assertEquals(-1, QuestionCommunication.askQuestion("Is there anybody?"));
    }

    @Test
    public void askQuestionServerRefusesTest() {
        stopServer();
        Lecture.setCurrentLecture(new Lecture(lid, modkey, "?", "???"));
        assertEquals(-2, QuestionCommunication.askQuestion("Will we get 10 for OOPP?"));
        startServer();
    }

    @Test
    public void askQuestionUnsuccessfulTest() {
        Lecture.setCurrentLecture(new Lecture(lid, modkey, "#", "$"));
        assertEquals(-3, QuestionCommunication.askQuestion("F*ck"));
    }

    /**
     * Tests for fetching.
     */

    @Test
    public void fetchQuestionsCurrentSuccessfulTest() {
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "HCI", "Not Sebastian"));
        List<Question> questions = QuestionCommunication.fetchQuestions();

        assertNotNull(questions);
        assertEquals(2, questions.size());

        Question expected1 = new Question(lid, "HHH", 42);
        Question actual1 = questions.get(0);
        Question expected2 = new Question(lid, "koiko", 69);
        Question actual2 = questions.get(1);

        assertEquals(expected1.getLectureId(), actual1.getLectureId());
        assertEquals(expected2.getLectureId(), actual2.getLectureId());

        assertEquals(expected1.getText(), actual1.getText());
        assertEquals(expected2.getText(), actual2.getText());

        assertEquals(0, actual1.getOwnerId());
        assertEquals(0, actual2.getOwnerId());

        assertEquals(Long.parseLong(qid1), actual1.getId());
        assertEquals(Long.parseLong(qid2), actual2.getId());
    }

    @Test
    public void fetchQuestionsCurrentNoLectureExistsTest() {
        Lecture.setCurrentLecture(null);
        assertNull(QuestionCommunication.fetchQuestions());
    }

    @Test
    public void fetchQuestionsNotFoundTest() {
        Lecture.setCurrentLecture(new Lecture(modkey,
                modkey, "Welcome to OOPP", "Sander"));
        assertEquals(new ArrayList<Question>(), QuestionCommunication.fetchQuestions());
    }

    @Test
    public void fetchQuestionsServerRefusesTest() {
        stopServer();
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "empty", "placeholder"));
        assertNull(QuestionCommunication.fetchQuestions());
        startServer();
    }

    @Test
    public void fetchQuestionsInvalidLectureIdTest() {
        Lecture.setCurrentLecture(new Lecture(incorrectModkey,
                modkey, "*", "Anonymous"));
        assertNull(QuestionCommunication.fetchQuestions());
    }

    /**
     * Tests for upvoteQuestion.
     */

    @Test
    public void upvoteQuestionSuccessfulTest() {
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "Spring Boot", "Sebastian"));
        assertEquals(0, QuestionCommunication.upvoteQuestion(Long.parseLong(qid1), 443));
    }

    @Test
    public void upvoteQuestionNoLectureExistsTest() {
        Lecture.setCurrentLecture(null);
        assertEquals(-1, QuestionCommunication.upvoteQuestion(Long.parseLong(qid1), 443));
    }

    @Test
    public void upvoteQuestionServerRefusesTest() {
        stopServer();
        Lecture.setCurrentLecture(new Lecture(lid, modkey,
                "Requirements engineering", "Sander"));
        assertEquals(-2, QuestionCommunication.upvoteQuestion(666, 666));
        startServer();
    }

    @Test
    public void upvoteQuestionInvalidUidTest() {
        Lecture.setCurrentLecture(new Lecture(lid, modkey,
                "Testing", "Sebastian"));
        assertEquals(-3,
                QuestionCommunication.upvoteQuestion(Long.parseLong(qid1), Long.MAX_VALUE));
    }

    @Test
    public void upvoteQuestionIncorrectQidTest() {
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "Testing", "Sebastian"));
        assertEquals(-4, QuestionCommunication.upvoteQuestion(666, 443));
    }

    @Test
    public void upvoteQuestionIncorrectUidTest() {
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "Gitlab", "Sander"));
        assertEquals(-4, QuestionCommunication.upvoteQuestion(Long.parseLong(qid1), 442));
    }

    /**
     * Tests for markAsAnswered.
     */

    @Test
    public void markAsAnsweredSuccessfulTest() {
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "Git", "Sebastian"));
        assertEquals(0, QuestionCommunication.markedAsAnswered(Long.parseLong(qid1), modkey));
    }

    @Test
    public void markAsAnsweredNoLectureExistsTest() {
        Lecture.setCurrentLecture(null);
        assertEquals(-1, QuestionCommunication.markedAsAnswered(Long.parseLong(qid1), modkey));
    }

    @Test
    public void markAsAnsweredServerRefusesTest() {
        stopServer();
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "Teamwork", "Not Sander"));
        assertEquals(-2, QuestionCommunication.markedAsAnswered(Long.parseLong(qid1), modkey));
        startServer();
    }

    @Test
    public void markAsAnsweredInvalidUUIDTest() {
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "Testing", "Andy"));
        assertEquals(-3,
                QuestionCommunication.markedAsAnswered(Long.parseLong(qid1), lid));
    }

    @Test
    public void markAsAnsweredIncorrectQidTest() {
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "RCS", "Not Sebastian"));
        assertEquals(-4, QuestionCommunication.markedAsAnswered(666, modkey));
    }

    @Test
    public void markAsAnsweredIncorrectModkeyTest() {
        Lecture.setCurrentLecture(new Lecture(lid,
                modkey, "Lambda expressions", "Thomas"));
        assertEquals(-4,
                QuestionCommunication.markedAsAnswered(Long.parseLong(qid1), incorrectModkey));
    }

    /**
     * Stops server.
     */
    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }
}
