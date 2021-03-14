package nl.tudelft.oopp.livechat.communication;

import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.mockserver.verify.VerificationTimes;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;

public class QuestionCommunicationTest {

    public static MockServerClient mockServer;


    private static final UUID uuid = UUID.fromString("dfabcfdf-271b-48d2-841e-4874ff28b4a6");
    private static final UUID modkey = UUID.randomUUID();

    /**
     * Starts mock server.
     */
    @BeforeAll
    public static void startServer() {

        String response = "[\n"
                + "    {\n"
                + "        \"id\": 1525501830961993525,\n"
                + "        \"lectureId\": \"dfabcfdf-271b-48d2-841e-4874ff28b4a6\",\n"
                + "        \"time\": \"2021-03-11T12:37:37.403+0000\",\n"
                + "        \"votes\": 0,\n"
                + "        \"text\": \"HHH\",\n"
                + "        \"answered\": false,\n"
                + "        \"answerText\": null,\n"
                + "        \"answerTime\": null\n"
                + "    },\n"
                + "    {\n"
                + "        \"id\": 6482091313835038158,\n"
                + "        \"lectureId\": \"dfabcfdf-271b-48d2-841e-4874ff28b4a6\",\n"
                + "        \"time\": \"2021-03-11T12:37:41.344+0000\",\n"
                + "        \"votes\": 0,\n"
                + "        \"text\": \"koiko\",\n"
                + "        \"answered\": false,\n"
                + "        \"answerText\": null,\n"
                + "        \"answerTime\": null\n"
                + "    }\n"
                + "]";


        final String  responseQuestionBody = "5397545054934456486";


        mockServer = ClientAndServer.startClientAndServer(8080);

        mockServer.when(request().withMethod("POST").withPath("/api/question/ask"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(responseQuestionBody)
                        .withHeader("Content-Type","application/json"));

        mockServer.when(request().withMethod("GET").withPath("/api/question/fetch")
                .withQueryStringParameter("lid", uuid.toString()))
                .respond(HttpResponse.response().withStatusCode(200)
                .withBody(response)
                .withHeader("Content-Type","application/json"));

        createExpectationsForUpvote();

    }

    private static void createExpectationsForUpvote() {
        //Success
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "5397545054934456486"),
                        new Parameter("uid", "443")))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //invalid uid - send 400
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "5397545054934456486"),
                        new Parameter("uid", String.valueOf(Long.MAX_VALUE))))
                .respond(HttpResponse.response().withStatusCode(400)
                        .withBody("-1").withHeader("Content-Type","application/json"));

        //incorrect uid
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "5397545054934456486"),
                        new Parameter("uid", "442")))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1").withHeader("Content-Type","application/json"));

        //incorrect qid
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "666"),
                        new Parameter("uid", "443")))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1").withHeader("Content-Type","application/json"));
    }


    @Test
    public void askQuestionSuccessfulTest() {
        Lecture res = new Lecture();
        Lecture.setCurrentLecture(res);
        assertEquals(1, QuestionCommunication.askQuestion("Is there anybody?"));
    }

    @Test
    public void askQuestionLectureNotExistsTest() {
        Lecture.setCurrentLecture(null);
        assertEquals(-1,QuestionCommunication.askQuestion("Is there anybody?"));
    }

    @Test
    public void fetchQuestionsCurrentLectureExistsTest() {
        Lecture.setCurrentLecture(new Lecture(uuid,
                modkey, "HCI", "Not Sebastian"));
        List<Question> questions = QuestionCommunication.fetchQuestions();
        assertNotNull(questions);
        assertEquals(2, questions.size());

        Question expected1 = new Question(uuid, "HHH", 42);
        Question expected2 = new Question(uuid, "koiko", 69);
        Question actual1 = questions.get(0);
        Question actual2 = questions.get(1);
        assertTrue(expected1.getLectureId().equals(actual1.getLectureId())
                        && expected1.getText().equals(actual1.getText())
                        && 0 == actual1.getOwnerId());
        assertTrue(expected2.getLectureId().equals(actual2.getLectureId())
                && expected2.getText().equals(actual2.getText())
                && 0 == actual2.getOwnerId());
    }

    @Test
    public void fetchQuestionsCurrentNoLectureExistsTest() {
        Lecture.setCurrentLecture(null);
        assertNull(QuestionCommunication.fetchQuestions());
    }

    @Test
    public void upvoteQuestionSuccessfulTest() {
        Lecture.setCurrentLecture(new Lecture(uuid,
                modkey, "Spring Boot", "Sebastian"));
        assertEquals(0, QuestionCommunication.upvoteQuestion(5397545054934456486L, 443));
    }

    @Test
    public void upvoteQuestionNoLectureExistsTest() {
        Lecture.setCurrentLecture(null);
        assertEquals(-1, QuestionCommunication.upvoteQuestion(5397545054934456486L, 443));
    }

    @Test
    public void upvoteQuestionServerRefusesTest() {
        stopServer();
        Lecture.setCurrentLecture(new Lecture(uuid,
                modkey, "RCS", "Sander"));
        assertEquals(-2, QuestionCommunication.upvoteQuestion(666, 443));
        startServer();
    }

    @Test
    public void upvoteQuestionIncorrectQidTest() {
        Lecture.setCurrentLecture(new Lecture(uuid,
                modkey, "Testing", "Sebastian"));
        assertEquals(-4, QuestionCommunication.upvoteQuestion(666, 443));
    }

    @Test
    public void upvoteQuestionTooInvalidUidTest() {
        Lecture.setCurrentLecture(new Lecture(uuid,
                modkey, "Testing", "Sebastian"));
        assertEquals(-3, QuestionCommunication.upvoteQuestion(5397545054934456486L, Long.MAX_VALUE));
    }

    @Test
    public void upvoteQuestionIncorrectUidTest() {
        Lecture.setCurrentLecture(new Lecture(uuid,
                modkey, "Gitlab", "Sander"));
        assertEquals(-4, QuestionCommunication.upvoteQuestion(666, 443));
    }

    /**
     * Stops server.
     */
    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }
}
