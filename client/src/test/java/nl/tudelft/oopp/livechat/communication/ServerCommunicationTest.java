package nl.tudelft.oopp.livechat.communication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;

import nl.tudelft.oopp.livechat.data.Lecture;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.Expectation;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;


public class ServerCommunicationTest {

    public static MockServerClient mockServer;


    /**
     * Starts mock server.
     */
    @BeforeAll
    public static void startServer() {

        String jsonResponse = "{\"uuid\":\"0ee81155-96fc-4045-bfe9-dd7ca714b5e8\",\"modkey\""
                + ":\"08843278-e8b8-4d51-992f-48c6aee44e27\""
                + ",\"name\":\"name\",\"creatorName\""
                + ":\"placeholder\",\"fasterCount\":0,"
                + "\"slowerCount\":0,\"frequency\""
                + ":60,\"startTime\":\"2021-03-04T15:49:"
                + "27.962+0000\",\"open\":true}";

        mockServer = ClientAndServer.startClientAndServer(8080);
        mockServer.when(request().withMethod("POST").withPath("/newLecture")
                .withQueryStringParameter("name","name"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonResponse)
                        .withHeader("Content-Type","application/json"));

        mockServer.when(request().withMethod("GET")
                .withPath("/api/get/0ee81155-96fc-4045-bfe9-dd7ca714b5e8"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonResponse)
                        .withHeader("Content-Type","application/json"));


    }

    @Test
    public void testCreateLectureNotNull() {
        assertNotNull(ServerCommunication.createLecture("name"));
    }

    @Test
    public void testLectureNameMatches() {
        Lecture res = ServerCommunication.createLecture("name");
        assertEquals("name", res.getName());
    }

    @Test
    public void joinLectureByIdLectureExists() {
        Lecture res = ServerCommunication.joinLectureById("0ee81155-96fc-4045-bfe9-dd7ca714b5e8");
        assertEquals("0ee81155-96fc-4045-bfe9-dd7ca714b5e8",res.getUuid());

    }

    @Test
    public void joinLectureByIdNotExist() {
        Lecture res = ServerCommunication
                .joinLectureById("zebra");
        assertNull(res);

    }


    /**
     * Stops server.
     */
    @AfterAll
    public static void stopServer() {

        mockServer.stop();

    }
}
