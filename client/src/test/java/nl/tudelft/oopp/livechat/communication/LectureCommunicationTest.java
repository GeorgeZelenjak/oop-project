package nl.tudelft.oopp.livechat.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;

/**
 * Class for Lecture communication tests.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LectureCommunicationTest {

    public static MockServerClient mockServer;
    public static String jsonLecture;
    public static String jsonUser;
    private static final String lid = "0ee81155-96fc-4045-bfe9-dd7ca714b5e8";
    private static final String modkey = "08843278-e8b8-4d51-992f-48c6aee44e27";
    private static final String incorrectModkey = UUID.randomUUID().toString();
    private static final String e = "2.718281828459045235360287471352662497757247093699959574966\n"
            + "967627724076630353547594571382178525166427427466391932003059\n"
            + "921817413596629043572900334295260595630738132328627943490763\n"
            + "233829880753195251019011573834187930702154089149934884167509\n"
             + "24476146066808226\n";
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());
    private static final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    /**
     * A helper method to assign JSON string lecture.
     */
    private static void assignJsonLecture() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("uuid",lid);
        node.put("modkey", modkey);
        node.put("name","An awesome lecture");
        node.put("creatorName","placeholder");
        node.put("slowerCount","0");
        node.put("frequency","60");
        node.put("startTime", simpleDateFormat.format(time));
        node.put("slowerCount","0");
        node.put("open","true");

        jsonLecture = node.toString();
    }

    private static void assignJsonUser() {
        User.setUid();
        User.setUserName("name");
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("userName", User.getUserName());
        node.put("uid", User.getUid());
        node.put("lectureId", lid);
        jsonUser = node.toString();

    }

    /**
     * Create expectations for creating a new lecture.
     */
    private static void createExpectationsForCreateLecture() {
        //Success
        mockServer.when(request().withMethod("POST").withPath("/api/newLecture")
                .withQueryStringParameter("name","An awesome lecture"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonLecture)
                        .withHeader("Content-Type","application/json"));

        //lecture name is too long
        mockServer.when(request().withMethod("POST").withPath("/api/newLecture")
                .withQueryStringParameter("name",e))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("")
                        .withHeader("Content-Type","application/json"));
    }

    /**
     * Create expectations for joining a lecture.
     */
    private static void createExpectationsForJoinLectureById() {
        //success
        mockServer.when(request().withMethod("GET")
                .withPath("/api/get/" + lid))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(jsonLecture)
                        .withHeader("Content-Type","application/json"));

        //bad request
        mockServer.when(request().withMethod("GET")
                .withPath("/api/get/zebra"))
                .respond(HttpResponse.response().withStatusCode(400));

        //no lecture exists
        mockServer.when(request().withMethod("GET")
                .withPath("/api/get/" + modkey))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("")
                        .withHeader("Content-Type","application/json"));

        mockServer.when(request().withMethod("POST")
                .withPath("/api/user/register")
                .withBody(jsonUser))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0"));
    }

    /**
     * Create expectations for validating a moderator.
     */
    private static void createExpectationsForValidateModerator() {
        //Success
        mockServer.when(request().withMethod("GET")
                .withPath("/api/validate/" + lid + "/" + modkey))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0"));

        //Invalid lecture id
        mockServer.when(request().withMethod("GET")
                .withPath("/api/validate/validUUID" + "/" + modkey))
                .respond(HttpResponse.response().withStatusCode(400)
                        .withBody("Invalid UUID"));

        //Invalid modkey
        mockServer.when(request().withMethod("GET")
                .withPath("/api/validate/" + lid + "/validModkey"))
                .respond(HttpResponse.response().withStatusCode(400)
                        .withBody("Invalid UUID"));

        //Incorrect modkey
        mockServer.when(request().withMethod("GET")
                .withPath("/api/validate/" + lid + "/" + incorrectModkey))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1"));
    }

    /**
     * Create expectations for closing a lecture.
     */
    private static void createExpectationsForCloseLecture() {
        //Success
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/close/" + lid + "/" + modkey).withBody(""))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0"));

        //Invalid lecture id
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/close/validUUID" + "/" + modkey).withBody(""))
                .respond(HttpResponse.response().withStatusCode(400)
                        .withBody("Invalid UUID"));

        //Invalid modkey
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/close/" + lid + "/validModkey").withBody(""))
                .respond(HttpResponse.response().withStatusCode(400)
                        .withBody("Invalid UUID"));

        //Incorrect modkey
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/close/" + lid + "/" + incorrectModkey).withBody(""))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("-1"));
    }

    //Starts the server and assigns expectations
    @BeforeAll
    private static void startServer() {
        assignJsonLecture();
        assignJsonUser();
        mockServer = ClientAndServer.startClientAndServer(8080);
        createExpectationsForCreateLecture();
        createExpectationsForJoinLectureById();
        createExpectationsForValidateModerator();
        createExpectationsForCloseLecture();
    }

    /**
     * Tests for creating a new lecture.
     */
    @Test
    public void createLectureSuccessfulTest() {
        Lecture res = LectureCommunication.createLecture("An awesome lecture",
                "Jegor", time, 10);
        assertNotNull(res);
        assertEquals(res.getName(), "An awesome lecture");
    }

    @Test
    public void createLectureTooLongNameTest() {
        Lecture res = LectureCommunication.createLecture(e,"Papa Double", time, 10);
        assertNull(res);
    }

    @Test
    public void createLectureServerRefusesTest() {
        stopServer();
        Lecture res = LectureCommunication.createLecture("How to get 10 for OOPP",
                "Long Island", time, 10);
        assertNull(res);
        startServer();
    }

    @Test
    public void createLectureWrongResponseTest() {
        Lecture res = LectureCommunication.createLecture("Babba booey!",
                "Mojito", time, 10);
        assertNull(res);
    }

    /**
     * Tests for joining a lecture.
     */
    @Test
    public void joinLectureByIdSuccessfulTest() {
        Lecture res = LectureCommunication.joinLectureById(lid);
        assertNotNull(res);
        assertEquals(lid, res.getUuid().toString());
    }

    @Test
    public void joinLectureByIdLectureNotExistTest() {
        Lecture res = LectureCommunication.joinLectureById(modkey);
        assertNull(res);
    }

    @Test
    public void joinLectureByIdInvalidUUIDTest() {
        Lecture res = LectureCommunication.joinLectureById("zebra");
        assertNull(res);
    }

    @Test
    public void joinLectureByIdServerRefusesTest() {
        stopServer();
        Lecture res = LectureCommunication.joinLectureById(lid);
        assertNull(res);
        startServer();
    }

    /**
     * Tests for validating a moderator.
     */

    @Test
    public void validateModeratorSuccessfulTest() {
        assertTrue(LectureCommunication.validateModerator(lid, modkey));
    }

    @Test
    public void validateModeratorInvalidLectureIdTest() {
        assertFalse(LectureCommunication.validateModerator("validUUID", modkey));
    }

    @Test
    public void validateModeratorInvalidModkeyTest() {
        assertFalse(LectureCommunication.validateModerator(lid, "validModkey"));
    }

    @Test
    public void validateModeratorLectureDoesNotExistTest() {
        assertFalse(LectureCommunication.validateModerator(modkey, modkey));
    }

    @Test
    public void validateModeratorUnsuccessfulTest() {
        assertFalse(LectureCommunication.validateModerator(lid, incorrectModkey));
    }

    @Test
    public void validateModeratorServerRefusesTest() {
        Lecture.setCurrentLecture(new Lecture());
        stopServer();
        assertFalse(LectureCommunication.validateModerator(lid, modkey));
        startServer();
    }

    /**
     * Tests for closing a lecture.
     */

    @Test
    public void closeLectureSuccessfulTest() {
        Lecture.setCurrentLecture(new Lecture());
        assertTrue(LectureCommunication.closeLecture(lid, modkey));
    }

    @Test
    public void closeLectureCurrentLectureIsNullTest() {
        Lecture.setCurrentLecture(null);
        assertFalse(LectureCommunication.closeLecture(lid, modkey));
    }

    @Test
    public void closeLectureInvalidLectureIdTest() {
        Lecture.setCurrentLecture(new Lecture());
        assertFalse(LectureCommunication.closeLecture("validUUID", modkey));
    }

    @Test
    public void closeLectureInvalidModkeyTest() {
        Lecture.setCurrentLecture(new Lecture());
        assertFalse(LectureCommunication.closeLecture(lid, "validModkey"));
    }

    @Test
    public void closeLectureLectureNotFoundTest() {
        Lecture.setCurrentLecture(new Lecture());
        assertFalse(LectureCommunication.closeLecture(modkey, modkey));
    }

    @Test
    public void closeLectureUnsuccessfulTest() {
        Lecture.setCurrentLecture(new Lecture());
        assertFalse(LectureCommunication.closeLecture(lid, incorrectModkey));
    }

    @Test
    public void closeLectureServerRefusesTest() {
        Lecture.setCurrentLecture(new Lecture());
        stopServer();
        assertFalse(LectureCommunication.closeLecture(lid, modkey));
        startServer();
    }

    //Stops the server
    @AfterAll
    private static void stopServer() {
        mockServer.stop();
    }
}
