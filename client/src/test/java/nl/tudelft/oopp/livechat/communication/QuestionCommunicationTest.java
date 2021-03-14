package nl.tudelft.oopp.livechat.communication;

import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;

public class QuestionCommunicationTest {

    public static MockServerClient mockServer;


    private static final UUID uuid = UUID.randomUUID();

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
                .withQueryStringParameter("lid",uuid.toString()))
                .respond(HttpResponse.response().withStatusCode(200)
                .withBody(response)
                .withHeader("Content-Type","application/json"));

        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "5397545054934456486"),
                        new Parameter("uid", "443")))
                .respond(HttpResponse.response().withStatusCode(200)
                .withBody("0").withHeader("Content-Type","application/json"));
    }



    @Test
    public void testAskQuestionLectureExists() {
        Lecture res = new Lecture();
        Lecture.setCurrentLecture(res);
        assertEquals(1, QuestionCommunication.askQuestion("Is there anybody?"));
    }

    @Test
    public void testAskQuestionLectureNotExists() {
        Lecture.setCurrentLecture(null);
        assertEquals(-1,QuestionCommunication.askQuestion("Is there anybody?"));
    }

    @Test
    public void testFetchQuestionsCurrentLectureNull() {
        Lecture.setCurrentLecture(null);
        assertNull(QuestionCommunication.fetchQuestions());
    }

    @Test
    public void testFetchQuestionsCurrentLectureNotNull() {
        Lecture.setCurrentLecture(new Lecture(uuid,
                null, "TEST", "NOT TEST"));
        assertNotNull(QuestionCommunication.fetchQuestions());
    }

    @Test
    public void testUpvoteQuestionSuccessful() {
        assertEquals(1, QuestionCommunication.upvoteQuestion(5397545054934456486L, 443));
    }

    @Test
    public void testUpvoteQuestionWrongQid() {
        assertEquals(-3, QuestionCommunication.upvoteQuestion(42, 443));
    }

    @Test
    public void testUpvoteQuestionWrongUid() {
        assertEquals(-3, QuestionCommunication.upvoteQuestion(5397545054934456486L, 442));
    }

    /**
     * Stops server.
     */
    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }
}
