package nl.tudelft.oopp.livechat.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockserver.model.HttpRequest.request;

/**
 * Class for Question communication tests.
 */
public class QuestionCommunicationTest {

    public static MockServerClient mockServer;

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
            .excludeFieldsWithoutExposeAnnotation().create();
    private static MockedStatic<AlertController> mockedAlertController;

    private static final UUID lid = UUID.randomUUID();
    private static final UUID modkey = UUID.randomUUID();
    private static final UUID incorrectModkey = UUID.randomUUID();
    private static final String qid1 = "5397545054934456486";
    private static final String qid2 = "8077505054105457480";
    private static final String qid3 = "6840541099020457076";
    private static long userId;
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    private static String goodQuestion;
    private static String normalQuestion;
    private static String badQuestion;

    private static String json1;
    private static String json2;
    private static String json3;
    private static String json4;

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

        //For testing other methods (different question id)
        mockServer.when(request().withMethod("POST").withPath("/api/question/ask")
                .withBody(normalQuestion))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(qid3)
                        .withHeader("Content-Type","application/json"));
    }

    /**
     * Create expectations for fetching questions.
     */
    private static void createExpectationsForFetching() {
        //Success
        String question1 = createJsonQuestion(qid1, "HHH");
        String question2 = createJsonQuestion(qid2, "koiko");
        String questions = createQuestionsList(question1, question2);
        mockServer.when(request().withMethod("GET").withPath("/api/question/fetch")
                .withQueryStringParameter("lid", lid.toString()))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(questions)
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
                        new Parameter("uid", "443")).withBody(""))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //invalid parameter - send 400
        // (treat Long.MAX_VALUE as invalid parameter to test BAD REQUEST)
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", qid1),
                        new Parameter("uid", String.valueOf(Long.MAX_VALUE))).withBody(""))
                .respond(HttpResponse.response().withStatusCode(400));

        //uid not found
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", qid1),
                        new Parameter("uid", "442")).withBody(""))
                .respond(HttpResponse.response().withStatusCode(409));

        //qid not found
        mockServer.when(request().withMethod("PUT").withPath("/api/question/upvote")
                .withQueryStringParameters(new Parameter("qid", "666"),
                        new Parameter("uid", "443")).withBody(""))
                .respond(HttpResponse.response().withStatusCode(404));
    }

    /**
     * Create expectations for marking question as answered.
     */
    private static void createExpectationsForMarkAsAnswered() {
        //Success without text
        mockServer.when(request().withMethod("PUT").withPath("/api/question/answer/"
                        + qid1 + "/" +  modkey).withBody(" "))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //Success with text
        mockServer.when(request().withMethod("PUT").withPath("/api/question/answer/"
                + qid1 + "/" +  modkey).withBody("42"))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //invalid parameter - send 400
        //  (treat lecture id (lid) as invalid UUID here to test BAD REQUEST)
        mockServer.when(request().withMethod("PUT").withPath("/api/question/answer/"
                        + qid1 + "/" +  lid).withBody(" "))
                .respond(HttpResponse.response().withStatusCode(400));

        //qid does not match
        mockServer.when(request().withMethod("PUT")
                .withPath("/api/question/answer/" + 666 + "/" +  modkey).withBody("answer"))
                .respond(HttpResponse.response().withStatusCode(404));

        //incorrect modkey
        mockServer.when(request().withMethod("PUT").withPath("/api/question/answer/"
                + qid1 + "/" +  incorrectModkey).withBody("the answer"))
                .respond(HttpResponse.response().withStatusCode(401));
    }

    /**
     * Create expectations for deleting own questions.
     */
    private static void createExpectationsForDeleteQuestion() {
        //Success
        mockServer.when(request().withMethod("DELETE").withPath("/api/question/delete")
                .withQueryStringParameters(new Parameter("qid", qid3),
                        new Parameter("uid", String.valueOf(userId))))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //invalid parameter - send 400
        // (treat Long.MAX_VALUE as invalid parameter to test BAD REQUEST)
        mockServer.when(request().withMethod("DELETE").withPath("/api/question/delete")
                .withQueryStringParameters(new Parameter("qid", qid1),
                        new Parameter("uid", String.valueOf(Long.MAX_VALUE))))
                .respond(HttpResponse.response().withStatusCode(400));

        //uid does not match
        mockServer.when(request().withMethod("DELETE").withPath("/api/question/delete")
                .withQueryStringParameters(new Parameter("qid", qid1),
                        new Parameter("uid", "442")))
                .respond(HttpResponse.response().withStatusCode(409));

        //qid not found
        mockServer.when(request().withMethod("DELETE").withPath("/api/question/delete")
                .withQueryStringParameters(new Parameter("qid", "666"),
                        new Parameter("uid", "443")))
                .respond(HttpResponse.response().withStatusCode(404));
    }

    /**
     * Create expectations for deleting any questions (done by moderator).
     */
    private static void createExpectationsForModDelete() {
        //Success
        mockServer.when(request().withMethod("DELETE").withPath("/api/question/moderator/delete")
                .withQueryStringParameters(new Parameter("qid", qid2),
                        new Parameter("modkey", String.valueOf(modkey))))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //invalid parameter - send 400
        //  (treat lecture id (lid) as invalid UUID here to test BAD REQUEST)
        mockServer.when(request().withMethod("DELETE").withPath("/api/question/moderator/delete")
                .withQueryStringParameters(new Parameter("qid", qid1),
                        new Parameter("modkey", String.valueOf(lid))))
                .respond(HttpResponse.response().withStatusCode(400));

        //modkey does not match
        mockServer.when(request().withMethod("DELETE").withPath("/api/question/moderator/delete")
                .withQueryStringParameters(new Parameter("qid", qid1),
                        new Parameter("modkey", String.valueOf(incorrectModkey))))
                .respond(HttpResponse.response().withStatusCode(401));

        //qid not found
        mockServer.when(request().withMethod("DELETE").withPath("/api/question/moderator/delete")
                .withQueryStringParameters(new Parameter("qid", "666"),
                        new Parameter("modkey", String.valueOf(modkey))))
                .respond(HttpResponse.response().withStatusCode(404));
    }

    /**
     * Create expectations for editing questions (done by moderator).
     */
    private static void createExpectationsForEdit() {
        //Success
        mockServer.when(request().withMethod("PUT").withPath("/api/question/edit")
                .withBody(json1)).respond(HttpResponse.response().withStatusCode(200)
                        .withBody("0").withHeader("Content-Type","application/json"));

        //invalid parameter - send 400
        //  (treat lecture id (lid) as invalid UUID here to test BAD REQUEST)
        mockServer.when(request().withMethod("PUT").withPath("/api/question/edit")
                .withBody(json2)).respond(HttpResponse.response().withStatusCode(400));

        //modkey does not match
        mockServer.when(request().withMethod("PUT").withPath("/api/question/edit")
                .withBody(json3)).respond(HttpResponse.response().withStatusCode(401));

        //qid not found
        mockServer.when(request().withMethod("PUT").withPath("/api/question/edit")
                .withBody(json4)).respond(HttpResponse.response().withStatusCode(404));
    }

    /**
     * Create expectations for editing questions (done by moderator).
     */
    private static void createExpectationsForSetStatus() {
        //Success
        mockServer.when(request().withMethod("PUT").withPath("/api/question/status/"
                + qid1 + "/" + userId + "/" + modkey.toString()).withBody("editing"))
                .respond(HttpResponse.response().withStatusCode(200)
                .withBody("0").withHeader("Content-Type","application/json"));

        //invalid parameter - send 400
        mockServer.when(request().withMethod("PUT").withPath("/api/question/status/"
                + qid1 + "/" + userId + "/" + modkey.toString()).withBody("invalid"))
                .respond(HttpResponse.response().withStatusCode(400));

        //modkey does not match
        mockServer.when(request().withMethod("PUT").withPath("/api/question/status/"
                + qid1 + "/" + userId + "/" + incorrectModkey.toString()).withBody("answering"))
                .respond(HttpResponse.response().withStatusCode(401));

        //qid not found
        mockServer.when(request().withMethod("PUT").withPath("/api/question/status/404/"
                 + userId + "/" + modkey.toString()).withBody("answering"))
                .respond(HttpResponse.response().withStatusCode(404));
    }

    /**
     * A helper method to create JSON object for edit request.
     * @param qid the id of the question
     * @param modkey the moderator key
     * @param text the new text
     * @param uid the user id
     * @return the JSON string with the provided values
     */
    private static String createJson(long qid, UUID modkey, String text, long uid) {
        //Create a json object with the data to be sent
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", qid);
        jsonObject.addProperty("modkey", modkey.toString());
        jsonObject.addProperty("text", text);
        jsonObject.addProperty("uid", uid);
        return gson.toJson(jsonObject);
    }

    private static String createJsonQuestion(String qid, String text) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("id", qid);
        node.put("lectureId", lid.toString());
        node.put("time", simpleDateFormat.format(time));
        node.put("votes", 0);
        node.put("text", text);
        return node.toString();
    }

    private static String createQuestionsList(String... questions) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < questions.length; i++) {
            sb.append(questions[i]);
            if (i < questions.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * Starts mock server.
     */
    @BeforeAll
    public static void startServer() {
        mockServer = ClientAndServer.startClientAndServer(8080);
        User.setUid();
        userId = User.getUid();
        goodQuestion = gson.toJson(
                new Question(lid, "Is there anybody?",  userId));
        normalQuestion = gson.toJson(
                new Question(lid, "Will we get 10?",  userId));
        badQuestion = gson.toJson(
                new Question(lid, "F*ck",  userId));

        json1 = createJson(Long.parseLong(qid1), modkey, "Edited", userId);
        json2 = createJson(Long.parseLong(qid2), lid, "Edited question", userId);
        json3 = createJson(Long.parseLong(qid3), incorrectModkey, "Edited by ...", userId);
        json4 = createJson(666, modkey, "Edited or not", userId);

        createExpectationsForAsking();
        createExpectationsForFetching();
        createExpectationsForUpvote();
        createExpectationsForMarkAsAnswered();
        createExpectationsForDeleteQuestion();
        createExpectationsForModDelete();
        createExpectationsForEdit();
        createExpectationsForSetStatus();

        try {
            mockedAlertController = Mockito.mockStatic(AlertController.class);
            mockedAlertController.when(() -> AlertController.alertError(any(String.class),
                    any(String.class))).thenAnswer((Answer<Void>) invocation -> null);
        } catch (Exception e) {
            System.out.println("Caught exception!");
        }
    }

    @AfterEach
    public void clear() {
        User.getAskedQuestionIds().clear();
    }


    /**
     * Tests for asking.
     */

    @Test
    public void askQuestionSuccessfulTest() {
        Lecture.setCurrent(new Lecture(lid, modkey, "Testing", "Andy"));
        int oldSize = User.getAskedQuestionIds().size();
        assertTrue(QuestionCommunication.askQuestion(
                User.getUid(),Lecture.getCurrent().getUuid(), "Is there anybody?"));
        assertTrue(User.getAskedQuestionIds().contains(Long.parseLong(qid1)));
        assertEquals(oldSize + 1, User.getAskedQuestionIds().size());
    }

    @Test
    public void askQuestionLectureNotExistsTest() {
        int oldSize = User.getAskedQuestionIds().size();
        Lecture.setCurrent(null);
        assertFalse(QuestionCommunication.askQuestion(
                User.getUid(),null,"Is there anybody?"));
        assertEquals(oldSize, User.getAskedQuestionIds().size());
    }

    @Test
    public void askQuestionServerRefusesTest() {
        mockServer.stop();

        int oldSize = User.getAskedQuestionIds().size();
        Lecture.setCurrent(new Lecture(lid, modkey, "?", "???"));
        assertFalse(QuestionCommunication.askQuestion(
                User.getUid(),Lecture.getCurrent().getUuid(),"Will we get 10 for OOPP?"));
        assertEquals(oldSize, User.getAskedQuestionIds().size());

        startServer();
    }

    @Test
    public void askQuestionUnsuccessfulTest() {
        int oldSize = User.getAskedQuestionIds().size();
        Lecture.setCurrent(new Lecture(lid, modkey, "#", "$"));
        assertFalse(QuestionCommunication.askQuestion(
                User.getUid(),Lecture.getCurrent().getUuid(),"F*ck"));
        assertEquals(oldSize, User.getAskedQuestionIds().size());
    }

    /**
     * Tests for fetching questions.
     */

    @Test
    public void fetchQuestionsCurrentSuccessfulTest() {
        Lecture.setCurrent(new Lecture(lid,
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
        Lecture.setCurrent(null);
        assertNull(QuestionCommunication.fetchQuestions());
    }

    @Test
    public void fetchQuestionsNotFoundTest() {
        Lecture.setCurrent(new Lecture(modkey,
                modkey, "Welcome to OOPP", "Sander"));
        assertEquals(new ArrayList<Question>(), QuestionCommunication.fetchQuestions());
    }

    @Test
    public void fetchQuestionsServerRefusesTest() {
        mockServer.stop();
        Lecture.setCurrent(new Lecture(lid,
                modkey, "empty", "placeholder"));
        assertNull(QuestionCommunication.fetchQuestions());

        startServer();
    }

    @Test
    public void fetchQuestionsInvalidLectureIdTest() {
        Lecture.setCurrent(new Lecture(incorrectModkey,
                modkey, "*", "Anonymous"));
        assertNull(QuestionCommunication.fetchQuestions());
    }

    /**
     * Tests for upvoting a question.
     */

    @Test
    public void upvoteQuestionSuccessfulTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Spring Boot", "Sebastian"));
        assertTrue(QuestionCommunication.upvoteQuestion(Long.parseLong(qid1), 443));
    }

    @Test
    public void upvoteQuestionNoLectureExistsTest() {
        Lecture.setCurrent(null);
        assertFalse(QuestionCommunication.upvoteQuestion(Long.parseLong(qid1), 443));
    }

    @Test
    public void upvoteQuestionServerRefusesTest() {
        mockServer.stop();
        Lecture.setCurrent(new Lecture(lid, modkey,
                "Requirements engineering", "Sander"));
        assertFalse(QuestionCommunication.upvoteQuestion(666, 666));

        startServer();
    }

    @Test
    public void upvoteQuestionInvalidUidTest() {
        Lecture.setCurrent(new Lecture(lid, modkey,
                "Testing", "Sebastian"));
        assertFalse(QuestionCommunication.upvoteQuestion(Long.parseLong(qid1), Long.MAX_VALUE));
    }

    @Test
    public void upvoteQuestionIncorrectQidTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Testing", "Sebastian"));
        assertFalse(QuestionCommunication.upvoteQuestion(666, 443));
    }

    @Test
    public void upvoteQuestionIncorrectUidTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Gitlab", "Sander"));
        assertFalse(QuestionCommunication.upvoteQuestion(Long.parseLong(qid1), 442));
    }

    /**
     * Tests for marking a question as answered.
     */

    @Test
    public void markAsAnsweredSuccessfulTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Git", "Sebastian"));
        assertTrue(QuestionCommunication.markedAsAnswered(Long.parseLong(qid1), modkey, null));
    }

    @Test
    public void markAsAnsweredSuccessfulWithAnswerTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Git", "Sebastian"));
        assertTrue(QuestionCommunication.markedAsAnswered(Long.parseLong(qid1), modkey, "42"));
    }

    @Test
    public void markAsAnsweredNoLectureExistsTest() {
        Lecture.setCurrent(null);
        assertFalse(QuestionCommunication.markedAsAnswered(
                Long.parseLong(qid1), modkey, "answer"));
    }

    @Test
    public void markAsAnsweredServerRefusesTest() {
        mockServer.stop();
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Teamwork", "Not Sander"));
        assertFalse(QuestionCommunication.markedAsAnswered(Long.parseLong(qid1), modkey," "));
        startServer();
    }

    @Test
    public void markAsAnsweredInvalidUUIDTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Testing", "Andy"));
        assertFalse(QuestionCommunication.markedAsAnswered(Long.parseLong(qid1), lid, "answer"));
    }

    @Test
    public void markAsAnsweredIncorrectQidTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "RCS", "Not Sebastian"));
        assertFalse(QuestionCommunication.markedAsAnswered(666, modkey, "answer"));
    }

    @Test
    public void markAsAnsweredIncorrectModkeyTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Lambda expressions", "Thomas"));
        assertFalse(QuestionCommunication.markedAsAnswered(Long.parseLong(qid1),
                incorrectModkey, "the answer"));
    }

    /**
     * Tests for deleting own questions.
     */

    @Test
    public void deleteQuestionSuccessfulTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Arrays", "Andy"));
        QuestionCommunication.askQuestion(
                User.getUid(),Lecture.getCurrent().getUuid(),"Will we get 10?");
        int oldSize = User.getAskedQuestionIds().size();

        assertTrue(QuestionCommunication.deleteQuestion(Long.parseLong(qid3), userId));
        assertEquals(oldSize - 1, User.getAskedQuestionIds().size());
        assertFalse(User.getAskedQuestionIds().contains(Long.parseLong(qid3)));
    }

    @Test
    public void deleteQuestionNoLectureExistsTest() {
        Lecture.setCurrent(null);
        User.getAskedQuestionIds().add(777L);
        int oldSize = User.getAskedQuestionIds().size();

        assertFalse(QuestionCommunication.deleteQuestion(Long.parseLong(qid1), 443));
        assertEquals(oldSize, User.getAskedQuestionIds().size());
    }

    @Test
    public void deleteQuestionServerRefusesTest() {
        mockServer.stop();
        User.getAskedQuestionIds().add(123789L);
        Lecture.setCurrent(new Lecture(lid, modkey,
                "Linked lists", "Ivo"));
        int oldSize = User.getAskedQuestionIds().size();

        assertFalse(QuestionCommunication.deleteQuestion(42, 42));
        assertEquals(oldSize, User.getAskedQuestionIds().size());

        startServer();
    }

    @Test
    public void deleteQuestionInvalidUidTest() {
        User.getAskedQuestionIds().add(123456789L);
        Lecture.setCurrent(new Lecture(lid, modkey,
                "Red-black trees", "Ivo"));
        int oldSize = User.getAskedQuestionIds().size();

        assertFalse(QuestionCommunication.deleteQuestion(Long.parseLong(qid1), Long.MAX_VALUE));
        assertEquals(oldSize, User.getAskedQuestionIds().size());

    }

    @Test
    public void deleteQuestionIncorrectQidTest() {
        User.getAskedQuestionIds().add(987654321L);
        Lecture.setCurrent(new Lecture(lid,
                modkey, "AVL trees", "Ivo"));
        int oldSize = User.getAskedQuestionIds().size();
        assertFalse(QuestionCommunication.deleteQuestion(666, 443));
        assertEquals(oldSize, User.getAskedQuestionIds().size());
    }

    @Test
    public void deleteQuestionIncorrectUidTest() {
        User.getAskedQuestionIds().add(69696969L);
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Multiway search trees", "Ivo"));
        int oldSize = User.getAskedQuestionIds().size();
        assertFalse(QuestionCommunication.deleteQuestion(Long.parseLong(qid1), 442));
        assertEquals(oldSize, User.getAskedQuestionIds().size());
    }

    /**
     * Tests for deleting questions by moderator.
     */

    @Test
    public void modDeleteSuccessfulTest() {
        Lecture.setCurrent(new Lecture(lid, modkey, "Assembly", "Otto"));
        QuestionCommunication.askQuestion(
                User.getUid(),Lecture.getCurrent().getUuid(),"Will we get 10?");

        assertTrue(QuestionCommunication.modDelete(Long.parseLong(qid2), modkey));
    }

    @Test
    public void modDeleteNoLectureExistsTest() {
        Lecture.setCurrent(null);
        assertFalse(QuestionCommunication.modDelete(Long.parseLong(qid1), modkey));
    }

    @Test
    public void modDeleteServerRefusesTest() {
        mockServer.stop();
        Lecture.setCurrent(new Lecture(lid, modkey, "CPU", "Koen"));

        assertFalse(QuestionCommunication.modDelete(42, modkey));

        startServer();
    }

    @Test
    public void modDeleteInvalidUUIDTest() {
        Lecture.setCurrent(new Lecture(lid, modkey, "Virtual memory", "Koen"));
        assertFalse(QuestionCommunication.modDelete(Long.parseLong(qid1), lid));
    }

    @Test
    public void modDeleteIncorrectQidTest() {
        Lecture.setCurrent(new Lecture(lid, modkey, "Memory", "Koen"));
        assertFalse(QuestionCommunication.modDelete(666, modkey));
    }

    @Test
    public void modDeleteIncorrectModkeyTest() {
        Lecture.setCurrent(new Lecture(lid, modkey, "Caching", "Koen"));
        assertFalse(QuestionCommunication.modDelete(Long.parseLong(qid1), incorrectModkey));
    }

    /**
     * Tests for editing questions.
     */

    @Test
    public void editSuccessfulTest() {
        Lecture.setCurrent(new Lecture(lid, modkey, "Indexes", "Asterios"));
        QuestionCommunication.askQuestion(User.getUid(),
                Lecture.getCurrent().getUuid(),"Is there anybody?");
        assertTrue(QuestionCommunication.edit(Long.parseLong(qid1), modkey, "Edited"));

    }

    @Test
    public void editNoLectureExistsTest() {
        Lecture.setCurrent(null);
        assertFalse(QuestionCommunication.edit(Long.parseLong(qid1), modkey, "Edited question"));
    }

    @Test
    public void editServerRefusesTest() {
        mockServer.stop();
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Transactions", "Asterios"));
        assertFalse(QuestionCommunication.edit(Long.parseLong(qid1), modkey,"Not edited"));

        startServer();
    }

    @Test
    public void editInvalidUUIDTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Query processing", "Christoph"));
        assertFalse(QuestionCommunication.edit(Long.parseLong(qid2), lid, "Edited question"));
    }

    @Test
    public void editIncorrectQidTest() {
        Lecture.setCurrent(new Lecture(lid, modkey, "SQL", "Christoph"));
        assertFalse(QuestionCommunication.edit(666, modkey, "Edited or not"));
    }

    @Test
    public void editIncorrectModkeyTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "Relational algebra", "Christoph"));
        assertFalse(QuestionCommunication.edit(Long.parseLong(qid3),
                        incorrectModkey, "Edited by ..."));
    }

    /**
     * Tests for setting status of the question.
     */

    @Test
    public void setStatusSuccessfulTest() {
        Lecture.setCurrent(new Lecture(lid, modkey,
                "placeholder", "placeholder"));
        assertTrue(QuestionCommunication.setStatus(Long.parseLong(qid1),
                modkey, "editing", userId));

    }

    @Test
    public void setStatusNoLectureExistsTest() {
        Lecture.setCurrent(null);
        assertFalse(QuestionCommunication.setStatus(Long.parseLong(qid1),
                modkey, "editing", userId));
    }

    @Test
    public void setStatusServerRefusesTest() {
        mockServer.stop();
        Lecture.setCurrent(new Lecture(lid,
                modkey, "placeholder", "placeholder"));
        assertFalse(QuestionCommunication.setStatus(Long.parseLong(qid1),
                modkey,"editing", userId));

        startServer();
    }

    @Test
    public void setStatusInvalidTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "placeholder", "placeholder"));
        assertFalse(QuestionCommunication.setStatus(Long.parseLong(qid1), lid, "invalid", userId));
    }

    @Test
    public void setStatusIncorrectQidTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "placeholder", "placeholder"));
        assertFalse(QuestionCommunication.setStatus(404, modkey, "answering", userId));
    }

    @Test
    public void setStatusIncorrectModkeyTest() {
        Lecture.setCurrent(new Lecture(lid,
                modkey, "placeholder", "placeholder"));
        assertFalse(QuestionCommunication.setStatus(Long.parseLong(qid1),
                        incorrectModkey, "answering", userId));
    }

    /**
     * Stops server.
     */
    @AfterAll
    public static void stopServer() {
        mockServer.stop();
        mockedAlertController.close();
    }
}
