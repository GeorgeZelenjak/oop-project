package nl.tudelft.oopp.livechat.communication;

import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication;
import nl.tudelft.oopp.livechat.controllers.gui.AlertController;
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

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockserver.model.HttpRequest.request;

public class CommonCommunicationTest {
    public static MockServerClient mockServer;
    private static MockedStatic<AlertController> mockedAlertController;


    private static void createExpectations() {
        //success
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "42"),
                        new Parameter("uid", "443")).withBody(""))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //bad request
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "666"),
                        new Parameter("uid", "443")).withBody(""))
                .respond(HttpResponse.response().withStatusCode(400));

        //not json
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "42"),
                        new Parameter("uid", "444")).withBody(""))
                .respond(HttpResponse.response().withStatusCode(404)
                        .withBody("error").withHeader("Content-Type","application/json"));

        //json
        String jason = "{\"error\": \"bad uid\",\"message\": \"very bad\",\"status\": \"400\"}";
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "42"),
                        new Parameter("uid", "666")).withBody(""))
                .respond(HttpResponse.response().withStatusCode(400)
                        .withBody(jason).withHeader("Content-Type","application/json"));

    }

    private static HttpRequest buildRequest(long qid, long uid) {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        return HttpRequest.newBuilder().PUT(body)
                .uri(URI.create(CommonCommunication.ADDRESS + "/api/question/upvote"
                        + "?qid=" + qid + "&uid=" + uid)).build();
    }

    private static void startServer() {
        mockServer = ClientAndServer.startClientAndServer(8080);
        createExpectations();
    }

    /**
     * Setup for the tests.
     */
    @BeforeAll
    public static void setUp() {
        startServer();
        try {
            mockedAlertController = Mockito.mockStatic(AlertController.class);
            mockedAlertController.when(() -> AlertController.alertError(any(String.class),
                    any(String.class))).thenAnswer((Answer<Void>) invocation -> null);
        } catch (Exception e) {
            System.err.println("Exception caught");
        }
    }

    @Test
    public void sendAndReceiveSuccessful() {
        assertNotNull(CommonCommunication.sendAndReceive(buildRequest(42, 443)));
    }

    @Test
    public void sendAndReceiveUnSuccessful() {
        assertNotNull(CommonCommunication.sendAndReceive(buildRequest(69, 443)));
    }

    @Test
    public void sendAndReceiveServerRefusesTest() {
        mockServer.stop();
        assertNull(CommonCommunication.sendAndReceive(buildRequest(42, 443)));

        startServer();
    }



    @Test
    public void handleResponseSuccessful() {
        assertEquals(0, CommonCommunication
                .handleResponse(CommonCommunication.sendAndReceive(buildRequest(42, 443))));
    }

    @Test
    public void handleResponseNull() {
        assertEquals(-1, CommonCommunication.handleResponse(null));
    }

    @Test
    public void handleResponseBadRequest() {
        assertEquals(-1, CommonCommunication
                .handleResponse(CommonCommunication.sendAndReceive(buildRequest(666, 443))));
    }

    @Test
    public void handleResponseNotJson() {
        assertEquals(-1, CommonCommunication
                .handleResponse(CommonCommunication.sendAndReceive(buildRequest(42, 444))));
    }

    @Test
    public void handleResponseJson() {
        assertEquals(-1, CommonCommunication
                .handleResponse(CommonCommunication.sendAndReceive(buildRequest(42, 666))));
    }



    @Test
    public void handleResponseNoAlertsSuccessful() {
        assertEquals(0, CommonCommunication
                .handleResponseNoAlerts(CommonCommunication
                        .sendAndReceive(buildRequest(42, 443))));
    }

    @Test
    public void handleResponseNoAlertsNull() {
        assertEquals(-1, CommonCommunication.handleResponseNoAlerts(null));
    }

    @Test
    public void handleResponseNoAlertsBadRequest() {
        assertEquals(-1, CommonCommunication
                .handleResponseNoAlerts(CommonCommunication
                        .sendAndReceive(buildRequest(666, 443))));
    }

    @Test
    public void handleResponseNoAlertsNotJson() {
        assertEquals(-1, CommonCommunication
                .handleResponseNoAlerts(CommonCommunication
                        .sendAndReceive(buildRequest(42, 444))));
    }

    @Test
    public void handleResponseNoAlertsJson() {
        assertEquals(-1, CommonCommunication
                .handleResponseNoAlerts(CommonCommunication
                        .sendAndReceive(buildRequest(42, 666))));
    }


    /**
     * Stops server.
     */
    @AfterAll
    public static void stopServer() {
        mockServer.stop();
        mockedAlertController.close();
        while (!mockServer.hasStopped(3,100L, TimeUnit.MILLISECONDS)) {
            System.out.println("Server has not stopped yet. Waiting until it fully stops");
        }
    }
}
