package nl.tudelft.oopp.livechat.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;

/**
 * Class for Lecture communication tests.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LectureCommunicationTest {

    public static MockServerClient mockServer;
    public static String jsonLecture;

    private static void assignJsonLecture() {
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

        jsonLecture = node.toString();
    }

    private static void createLectureExpectations() {
        mockServer.when(request().withMethod("POST").withPath("/api/newLecture")
                .withQueryStringParameter("name","name"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonLecture)
                        .withHeader("Content-Type","application/json"));
    }

    private static void joinLectureByIdExpectations() {
        mockServer.when(request().withMethod("GET")
                .withPath("/api/get/0ee81155-96fc-4045-bfe9-dd7ca714b5e8"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonLecture)
                        .withHeader("Content-Type","application/json"));
    }

    private static void validateModeratorExpectations() {
        mockServer.when(request().withMethod("GET")
                .withPath("/api/validate/112/123"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0"));
    }

    private static void closeLectureExpectations() {
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/close/12/69"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0"));
    }

    //Starts the server and assigns expectations
    @BeforeAll
    private static void startServer() {
        assignJsonLecture();
        mockServer = ClientAndServer.startClientAndServer(8080);
        createLectureExpectations();
        joinLectureByIdExpectations();
        validateModeratorExpectations();
        closeLectureExpectations();
    }

    @Test
    public void createLectureNotNullTest() {
        assertNotNull(LectureCommunication.createLecture("name"));
    }

    @Test
    public void createLectureWrongResponse() {
        Lecture res = LectureCommunication.createLecture("Babba booey!");
        assertNull(res);
    }

    @Test
    public void createLectureNameMatchesTest() {
        Lecture res = LectureCommunication.createLecture("name");
        assertNotNull(res);
        assertEquals("name", res.getName());
    }

    @Test
    public void createLectureNoResponseTest() {
        stopServer();
        assertNull(LectureCommunication.createLecture("name"));
        startServer();
    }

    @Test
    public void joinLectureByIdLectureExistsTest() {
        Lecture res = LectureCommunication.joinLectureById("0ee81155-96fc-4045-bfe9-dd7ca714b5e8");
        assertNotNull(res);
        assertEquals("0ee81155-96fc-4045-bfe9-dd7ca714b5e8", res.getUuid().toString());
    }

    @Test
    public void joinLectureByIdLectureNotExistTest() {
        Lecture res = LectureCommunication.joinLectureById("zebra");
        assertNull(res);
    }

    @Test
    public void joinLectureByIdNoResponseTest() {
        stopServer();
        Lecture res = LectureCommunication.joinLectureById(
                "0ee81155-96fc-4045-bfe9-dd7ca714b5e8");
        assertNull(res);
        startServer();
    }


    @Test
    public void validateModeratorPassTest() {
        assertTrue(LectureCommunication.validateModerator("112","123"));
    }

    @Test
    public void validateModeratorFailTest() {
        assertFalse(LectureCommunication.validateModerator("not zebra","Zebra"));
    }

    @Test
    public void validateModeratorNoResponseTest() {
        stopServer();
        assertFalse(LectureCommunication.validateModerator("not zebra","Zebra"));
        startServer();
    }

    @Test
    public void closeLecturePassTest() {
        Lecture.setCurrentLecture(new Lecture());
        assertTrue(LectureCommunication.closeLecture("12","69"));
    }

    @Test
    public void closeLectureFailTest() {
        assertFalse(LectureCommunication.closeLecture("112","123"));
    }

    @Test
    public void closeLectureNoResponseTest() {
        stopServer();
        assertFalse(LectureCommunication.closeLecture("112","123"));
        startServer();
    }

    //Stops the server
    @AfterAll
    private static void stopServer() {
        mockServer.stop();
    }
}
