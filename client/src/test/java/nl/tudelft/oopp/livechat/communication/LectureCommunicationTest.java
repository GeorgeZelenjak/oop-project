package nl.tudelft.oopp.livechat.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;

/**
 * Class for Lecture communication tests.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LectureCommunicationTest {

    public static MockServerClient mockServer;
    public static boolean stopServer = false;

    /**
     * Starts mock server.
     */
    @BeforeAll
    public static void startServer() {

        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("uuid","0ee81155-96fc-4045-bfe9-dd7ca714b5e8");
        node.put("modkey","08843278-e8b8-4d51-992f-48c6aee44e27");
        node.put("name","name");
        node.put("creatorName","placeholder");
        node.put("slowerCount","0");
        node.put("frequency","60");
        node.put("startTime","2021-03-04T15:49:27.962+0000");
        node.put("slowerCount","0");
        node.put("open","true");

        String jsonResponseLecture = node.toString();
        final String  responseQuestionBody = "5397545054934456486";

        mockServer = ClientAndServer.startClientAndServer(8080);
        mockServer.when(request().withMethod("POST").withPath("/api/newLecture")
                .withQueryStringParameter("name","name"), Times.exactly(2))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonResponseLecture)
                        .withHeader("Content-Type","application/json"));

        mockServer.when(request().withMethod("GET")
                .withPath("/api/get/0ee81155-96fc-4045-bfe9-dd7ca714b5e8"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonResponseLecture)
                        .withHeader("Content-Type","application/json"));

        mockServer.when(request().withMethod("POST").withPath("/api/question/ask"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(responseQuestionBody)
                        .withHeader("Content-Type","application/json"));

        mockServer.when(request().withMethod("GET")
                .withPath("/api/validate/112/123"))
                .respond(HttpResponse.response().withStatusCode(200)
                .withBody("0"));

        mockServer.when(request().withMethod("PUT")
                .withPath("/api/close/12/69"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0"));

        mockServer.verify(request(), VerificationTimes.exactly(0));
    }

    @Test
    @Order(1)
    public void testCreateLectureNotNull() {
        assertNotNull(LectureCommunication.createLecture("name"));
    }

    @Test
    @Order(2)
    public void testLectureNameMatches() {
        Lecture res = LectureCommunication.createLecture("name");
        assertNotNull(res);
        assertEquals("name", res.getName());
    }

    @Test
    @Order(3)
    public void joinLectureByIdLectureExists() {
        Lecture res = LectureCommunication.joinLectureById("0ee81155-96fc-4045-bfe9-dd7ca714b5e8");
        assertNotNull(res);
        assertEquals("0ee81155-96fc-4045-bfe9-dd7ca714b5e8", res.getUuid().toString());
    }

    @Test
    @Order(4)
    public void joinLectureByIdNotExist() {
        Lecture res = LectureCommunication.joinLectureById("zebra");
        assertNull(res);
    }

    @Test
    @Order(5)
    public void validateModeratorPass() {
        assertTrue(LectureCommunication.validateModerator("112","123"));
    }

    @Test
    @Order(6)
    public void validateModeratorFail() {
        assertFalse(LectureCommunication.validateModerator("not zebra","Zebra"));
    }

    @Test
    @Order(7)
    public void closeLecturePass() {
        Lecture.setCurrentLecture(new Lecture());
        assertTrue(LectureCommunication.closeLecture("12","69"));
    }

    @Test
    @Order(8)
    public void closeLectureFail() {
        assertFalse(LectureCommunication.closeLecture("112","123"));
    }

    @Test
    @Order(9)
    public void testCreateLectureNoRespose() {
        assertNull(LectureCommunication.createLecture("name"));
    }

    /**
     * Stops server.
     */
    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }
}
