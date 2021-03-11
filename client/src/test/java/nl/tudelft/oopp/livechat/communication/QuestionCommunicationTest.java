package nl.tudelft.oopp.livechat.communication;

import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;

public class QuestionCommunicationTest {

    public static MockServerClient mockServer;


    /**
     * Starts mock server.
     */
    @BeforeAll
    public static void startServer() {



        String jsonResponseLecture = "{\"uuid\":\"0ee81155-96fc-4045-bfe9-dd7ca714b5e8\",\"modkey\""
                + ":\"08843278-e8b8-4d51-992f-48c6aee44e27\""
                + ",\"name\":\"name\",\"creatorName\""
                + ":\"placeholder\",\"fasterCount\":0,"
                + "\"slowerCount\":0,\"frequency\""
                + ":60,\"startTime\":\"2021-03-04T15:49:"
                + "27.962+0000\",\"open\":true}";

        final String  responseQuestionBody = "5397545054934456486";


        mockServer = ClientAndServer.startClientAndServer(8080);

        mockServer.when(request().withMethod("POST").withPath("/api/question/ask"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(responseQuestionBody)
                        .withHeader("Content-Type","application/json"));





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

    /**
     * Stops server.
     */
    @AfterAll
    public static void stopServer() {

        mockServer.stop();

    }
}
