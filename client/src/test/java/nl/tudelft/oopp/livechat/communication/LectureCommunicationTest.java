package nl.tudelft.oopp.livechat.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.LectureCommunication;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockserver.model.HttpRequest.request;

/**
 * Class for Lecture communication tests.
 */
public class LectureCommunicationTest {

    private static MockServerClient mockServer;
    private static String jsonLecture;
    private static String jsonUser;
    private static String jsonBanning;
    private static final String lid = UUID.randomUUID().toString();
    private static final String modkey = UUID.randomUUID().toString();
    private static final String incorrectModkey = UUID.randomUUID().toString();
    private static final String e = "2.718281828459045235360287471352662497757247093699959574966\n"
            + "967627724076630353547594571382178525166427427466391932003059\n"
            + "921817413596629043572900334295260595630738132328627943490763\n"
            + "233829880753195251019011573834187930702154089149934884167509\n"
             + "24476146066808226\n";
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());
    private static final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    private static MockedStatic<AlertController> mockedAlertController;

    /**
     * A helper method to assign JSON string lecture.
     */
    private static String createJsonLecture() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("uuid", lid);
        node.put("modkey", modkey);
        node.put("name","An awesome lecture");
        node.put("creatorName","placeholder");
        node.put("slowerCount","0");
        node.put("frequency","60");
        node.put("startTime", simpleDateFormat.format(time));
        node.put("slowerCount","0");
        node.put("open","true");
        return node.toString();
    }

    private static String createJsonUser(long uid, String name) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("userName", name);
        node.put("uid", uid);
        node.put("lectureId", lid);
        return node.toString();
    }

    private static String createJsonForBanning(String modkey) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("modid", User.getUid());
        node.put("modkey", modkey);
        node.put("qid", 42);
        node.put("time", 7);
        return node.toString();
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
                .withQueryStringParameter("name", e))
                .respond(HttpResponse.response().withStatusCode(400));
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
                .respond(HttpResponse.response().withStatusCode(400));
    }

    /**
     * Create expectations for joining a lecture.
     */
    private static void createExpectationsForRegisterUser() {
        //Success
        mockServer.when(request().withMethod("POST")
                .withPath("/api/user/register")
                .withBody(jsonUser))
                .respond(HttpResponse.response().withStatusCode(200));

        //Bad username
        mockServer.when(request().withMethod("POST")
                .withPath("/api/user/register")
                .withBody(createJsonUser(User.getUid(), e)))
                .respond(HttpResponse.response().withStatusCode(400));
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
                .respond(HttpResponse.response().withStatusCode(400));

        //Invalid modkey
        mockServer.when(request().withMethod("GET")
                .withPath("/api/validate/" + lid + "/validModkey"))
                .respond(HttpResponse.response().withStatusCode(400));

        //Incorrect modkey
        mockServer.when(request().withMethod("GET")
                .withPath("/api/validate/" + lid + "/" + incorrectModkey))
                .respond(HttpResponse.response().withStatusCode(400));
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
                .respond(HttpResponse.response().withStatusCode(400));

        //Invalid modkey
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/close/" + lid + "/validModkey").withBody(""))
                .respond(HttpResponse.response().withStatusCode(400));

        //Incorrect modkey
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/close/" + lid + "/" + incorrectModkey).withBody(""))
                .respond(HttpResponse.response().withStatusCode(400));
    }

    /**
     * Create expectations for closing a lecture.
     */
    private static void createExpectationsForBanning() {
        //Success by id
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/user/ban/id").withBody(jsonBanning))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0"));
        //Success by ip
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/user/ban/ip").withBody(jsonBanning))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0"));

        //Incorrect modkey for id
        String incorrectJsonBanning = createJsonForBanning(incorrectModkey);
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/user/ban/id").withBody(incorrectJsonBanning))
                .respond(HttpResponse.response().withStatusCode(401));

        //Invalid modkey for ip
        String invalidJsonBanning = createJsonForBanning("ValidModkey");
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/user/ban/ip").withBody(invalidJsonBanning))
                .respond(HttpResponse.response().withStatusCode(400));
    }

    /**
     * Starts the server and assigns expectations.
     */
    @BeforeAll
    public static void startServer() {
        User.setUid();
        User.setUserName("name");

        jsonLecture = createJsonLecture();
        jsonUser = createJsonUser(User.getUid(), User.getUserName());
        jsonBanning = createJsonForBanning(modkey);

        mockServer = ClientAndServer.startClientAndServer(8080);

        createExpectationsForCreateLecture();
        createExpectationsForJoinLectureById();
        createExpectationsForRegisterUser();
        createExpectationsForValidateModerator();
        createExpectationsForCloseLecture();
        createExpectationsForBanning();

        try {
            mockedAlertController = Mockito.mockStatic(AlertController.class);
            mockedAlertController.when(() -> AlertController.alertError(any(String.class),
                    any(String.class))).thenAnswer((Answer<Void>) invocation -> null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void createLectureUserNotRegisteredTest() {
        User.setUserName(e);
        Lecture res = LectureCommunication.createLecture("An awesome lecture",
                "Jegor", time, 10);
        assertNull(res);

        User.setUserName("name");
    }

    @Test
    public void createLectureTooLongNameTest() {
        Lecture res = LectureCommunication.createLecture(e,"Papa Double", time, 10);
        assertNull(res);
    }

    @Test
    public void createLectureServerRefusesTest() {
        mockServer.stop();
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
    public void joinLectureByIdUserNotRegisteredTest() {
        User.setUserName(e);
        Lecture res = LectureCommunication.joinLectureById(lid);
        assertNull(res);

        User.setUserName("name");
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
        mockServer.stop();
        Lecture res = LectureCommunication.joinLectureById(lid);
        assertNull(res);

        startServer();
    }

    @Test
    public void registerUserUnsuccessfulTest() {
        User.setUserName(e);
        assertFalse(LectureCommunication.registerUser(lid));

        User.setUserName("name");
    }

    @Test
    public void registerUserSuccessfulTest() {
        assertTrue(LectureCommunication.registerUser(lid));
    }

    @Test
    public void registerUserServerRefusesTest() {
        mockServer.stop();
        assertFalse(LectureCommunication.registerUser(lid));

        startServer();
    }

    //TODO REMOVE THE FOLLOWING 3 TESTS WHEN WE REMOVE THE DEBUG SCENE

    @Test
    public void registerUserDebugUnsuccessfulTest() {
        assertFalse(LectureCommunication.registerUserdebug(lid, User.getUid(), e));
    }

    @Test
    public void registerUserDebugSuccessfulTest() {
        assertTrue(LectureCommunication.registerUserdebug(lid, User.getUid(), User.getUserName()));
    }

    @Test
    public void registerUserDebugServerRefusesTest() {
        mockServer.stop();
        assertFalse(LectureCommunication.registerUserdebug(lid, User.getUid(), User.getUserName()));

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
        Lecture.setCurrent(new Lecture());
        mockServer.stop();
        assertFalse(LectureCommunication.validateModerator(lid, modkey));

        startServer();
    }

    /**
     * Tests for closing a lecture.
     * */


    @Test
    public void closeLectureSuccessfulTest() {
        Lecture.setCurrent(new Lecture());
        assertTrue(LectureCommunication.closeLecture(lid, modkey));
    }

    @Test
    public void closeLectureCurrentLectureIsNullTest() {
        Lecture.setCurrent(null);
        assertFalse(LectureCommunication.closeLecture(lid, modkey));
    }

    @Test
    public void closeLectureInvalidLectureIdTest() {
        Lecture.setCurrent(new Lecture());
        assertFalse(LectureCommunication.closeLecture("validUUID", modkey));
    }

    @Test
    public void closeLectureInvalidModkeyTest() {
        Lecture.setCurrent(new Lecture());
        assertFalse(LectureCommunication.closeLecture(lid, "validModkey"));
    }

    @Test
    public void closeLectureLectureNotFoundTest() {
        Lecture.setCurrent(new Lecture());
        assertFalse(LectureCommunication.closeLecture(modkey, modkey));
    }

    @Test
    public void closeLectureUnsuccessfulTest() {
        Lecture.setCurrent(new Lecture());
        assertFalse(LectureCommunication.closeLecture(lid, incorrectModkey));
    }

    @Test
    public void closeLectureServerRefusesTest() {
        Lecture.setCurrent(new Lecture());
        mockServer.stop();
        assertFalse(LectureCommunication.closeLecture(lid, modkey));

        startServer();
    }

    /**
     * Tests for banning.
     */

    @Test
    public void banByIpSuccessfulTest() {
        Lecture.setCurrent(new Lecture());
        assertTrue(LectureCommunication.ban(modkey, 42,7, true));
    }

    @Test
    public void banByIdSuccessfulTest() {
        Lecture.setCurrent(new Lecture());
        assertTrue(LectureCommunication.ban(modkey, 42,7, false));
    }

    @Test
    public void banByIdIncorrectModkeyTest() {
        Lecture.setCurrent(new Lecture());
        assertFalse(LectureCommunication.ban(incorrectModkey, 42,7, false));
    }

    @Test
    public void banByIpInvalidModkeyTest() {
        Lecture.setCurrent(new Lecture());
        assertFalse(LectureCommunication.ban("ValidModkey", 42,7, true));
    }

    @Test
    public void banNoLectureTest() {
        Lecture.setCurrent(null);
        assertFalse(LectureCommunication.ban(modkey, 42,7, true));
    }

    @Test
    public void banServerRefusesTest() {
        Lecture.setCurrent(new Lecture());
        mockServer.stop();
        assertFalse(LectureCommunication.ban(modkey, 42,7, true));

        startServer();
    }

    /**
     * Stops the server and closes mock alert controller.
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
