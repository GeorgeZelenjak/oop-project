package nl.tudelft.oopp.livechat.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.sql.Timestamp;
import java.util.List;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Class for Question controller tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    QuestionEntity q1;
    QuestionEntity q2;
    LectureEntity lectureEntity1;
    LectureEntity lectureEntity2;
    String q1Json;
    String q2Json;

    @BeforeEach
    void setup() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        String lecture1 = this.mockMvc.perform(post("/api/newLecture?name=test1"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        lectureEntity1 = objectMapper.readValue(lecture1, LectureEntity.class);
        String lecture2 = this.mockMvc.perform(post("/api/newLecture?name=test2"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        lectureEntity2 = objectMapper.readValue(lecture2, LectureEntity.class);
        q1 = new QuestionEntity(lectureEntity1.getUuid(),
                "What would you do if a seagull entered in your house?",
                        new Timestamp(System.currentTimeMillis()), 42);
        q2 = new QuestionEntity(lectureEntity2.getUuid(),
                "What would you do if a pelican entered in your house?",
                        new Timestamp(System.currentTimeMillis()), 69);
        q1Json = objectMapper.writeValueAsString(q1);
        JsonNode node1 = objectMapper.readTree(q1Json);
        node1 = ((ObjectNode) node1).put("ownerId", q1.getOwnerId());
        q1Json = node1.toString();

        q2Json = objectMapper.writeValueAsString(q2);
        JsonNode node2 = objectMapper.readTree(q2Json);
        node2 = ((ObjectNode) node2).put("ownerId", q2.getOwnerId());
        q2Json = node2.toString();
    }


    /**
     * A method to post questions.
     * @param question JSON representation of question entity
     * @return id of the new question
     * @throws Exception if something goes wrong
     */
    String postQuestions(String question) throws Exception {
        return this.mockMvc
                .perform(post("/api/question/ask")
                        .contentType(APPLICATION_JSON)
                        .content(question)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * A method to get all the questions associated with the lecture.
     * @param lectureId id of the lecture
     * @return list of question entities associated with the lecture
     * @throws Exception if something goes wrong
     */
    List<QuestionEntity> getQuestions(String lectureId) throws Exception {
        String listLectureString = this.mockMvc
                .perform(get("/api/question/fetch?lid=" + lectureId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(listLectureString,
                new TypeReference<>(){});
    }

    /**
     * A method to delete a question.
     * @param url url with question id and owner id/moderator id values
     * @return 0 if successful, otherwise -1
     * @throws Exception if something goes wrong
     */
    int deleteQuestion(String url) throws Exception {
        String result = this.mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return Integer.parseInt(result);
    }

    /**
     * A method to edit questions.
     * @param bodyJson JSON representation of parameters used to edit
     * @return 0 if successful, -1 otherwise
     * @throws Exception if something goes wrong
     */
    int editQuestion(String bodyJson) throws  Exception {
        String result = this.mockMvc
                .perform(put("/api/question/edit")
                        .contentType(APPLICATION_JSON)
                        .content(bodyJson)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return Integer.parseInt(result);
    }

    /**
     * A method to upvote a question.
     * @param qid id of the question
     * @param uid id of the user
     * @return 0 if successful, otherwise -1
     * @throws Exception if something goes wrong
     */
    int upvote(long qid, long uid) throws Exception {
        String result = this.mockMvc.perform(put("/api/question/upvote?qid=" + qid + "&uid=" + uid))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return Integer.parseInt(result);
    }

    /**
     * A method to anwer a question.
     *
     * @param qid id of the question
     * @param modkey the modkey
     * @return 0 if successful, otherwise -1
     * @throws Exception if something goes wrong
     */
    int answer(long qid, String modkey) throws Exception {
        String result = this.mockMvc.perform(put("/api/question/answer/" + qid + "/" + modkey)
                .contentType(APPLICATION_JSON)
                .content("This is definitely a question asnwer dude")
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return Integer.parseInt(result);
    }

    @Test
    void askQuestionSuccessfulTest() throws Exception {
        String qid1string = postQuestions(q1Json);
        long qid1 = Long.parseLong(qid1string);
        assertTrue(qid1 > 0);
    }

    @Test
    void askQuestionUnsuccessfulTest() throws Exception {
        postQuestions(q1Json);

        //check that 2 questions with the same id cannot bee asked
        q2.setId(q1.getId());
        String q2edited = objectMapper.writeValueAsString(q2);
        String result = postQuestions(q2edited);
        assertEquals("-1", result);
    }

    @Test
    void fetchQuestionsTest() throws Exception {
        final long qid1 = Long.parseLong(postQuestions(q1Json));
        final long qid2 = Long.parseLong(postQuestions(q2Json));

        List<QuestionEntity> listLecture1 = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2 = getQuestions(lectureEntity2.getUuid().toString());

        assertEquals(1, listLecture1.size());
        assertEquals(1, listLecture2.size());

        assertEquals(qid1, listLecture1.get(0).getId());
        assertEquals(qid2, listLecture2.get(0).getId());
    }

    @Test
    void fetchQuestionsFakeIdTest() throws Exception {
        this.mockMvc
                .perform(get("/api/question/fetch?lid=" + "something_wrong"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteQuestionSuccessfulTest() throws Exception {
        long qid1 = Long.parseLong(postQuestions(q1Json));
        postQuestions(q2Json);


        int result = deleteQuestion("/api/question/delete?qid=" + qid1
                                                + "&uid=" + q1.getOwnerId());
        assertEquals(0, result);

        List<QuestionEntity> listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        assertEquals(0, listLecture1after.size());
        assertEquals(1, listLecture2after.size());
    }

    @Test
    void deleteQuestionUnsuccessfulTest() throws Exception {
        long qid1 = Long.parseLong(postQuestions(q1Json));
        postQuestions(q2Json);

        int result = deleteQuestion("/api/question/delete?qid=" + qid1
                                                + "&uid=" + q2.getOwnerId());
        assertEquals(-1, result);

        List<QuestionEntity> listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        assertEquals(1, listLecture1after.size());
        assertEquals(1, listLecture2after.size());
    }

    @Test
    void modDeleteSuccessfulTest() throws Exception {
        long qid1 = Long.parseLong(postQuestions(q1Json));
        postQuestions(q2Json);

        int result = deleteQuestion("/api/question/moderator/delete?qid="
                + qid1 + "&modkey=" + lectureEntity1.getModkey().toString());
        assertEquals(0, result);

        List<QuestionEntity> listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        assertEquals(0, listLecture1after.size());
        assertEquals(1, listLecture2after.size());
    }

    @Test
    void modDeleteUnsuccessfulTest() throws Exception {
        long qid1 = Long.parseLong(postQuestions(q1Json));
        postQuestions(q2Json);

        final int result = deleteQuestion("/api/question/moderator/delete?qid=" + qid1
                + "&modkey=" + lectureEntity2.getModkey().toString());
        assertEquals(-1, result);

        List<QuestionEntity> listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        assertEquals(1, listLecture1after.size());
        assertEquals(1, listLecture2after.size());
    }

    @Test
    void upvoteSuccessfulTest() throws Exception {
        final long qid1 = Long.parseLong(postQuestions(q1Json));
        postQuestions(q2Json);

        List<QuestionEntity> listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        final int oldVotes1 = listLecture1after.get(0).getVotes();
        final int oldVotes2 = listLecture2after.get(0).getVotes();

        final int result = upvote(qid1, q1.getOwnerId());
        assertEquals(0, result);

        listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        int newVotes1 = listLecture1after.get(0).getVotes();
        int newVotes2 = listLecture2after.get(0).getVotes();

        assertEquals(oldVotes1 + 1, newVotes1);
        assertEquals(oldVotes2, newVotes2);
    }

    @Test
    void unvoteSuccessfulTest() throws Exception {
        long qid1 = Long.parseLong(postQuestions(q1Json));
        postQuestions(q2Json);

        List<QuestionEntity> listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        final int oldVotes1 = listLecture1after.get(0).getVotes();
        final int oldVotes2 = listLecture2after.get(0).getVotes();

        upvote(qid1, q1.getOwnerId());
        final int result = upvote(qid1, q1.getOwnerId());
        assertEquals(0, result);

        listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        int newVotes1 = listLecture1after.get(0).getVotes();
        int newVotes2 = listLecture2after.get(0).getVotes();

        assertEquals(oldVotes1, newVotes1);
        assertEquals(oldVotes2, newVotes2);
    }

    @Test
    void upvoteUnsuccessfulTest() throws Exception {
        postQuestions(q1Json);
        long qid1 = -1;
        postQuestions(q2Json);

        List<QuestionEntity> listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        final int oldVotes1 = listLecture1after.get(0).getVotes();
        final int oldVotes2 = listLecture2after.get(0).getVotes();

        upvote(qid1, q1.getOwnerId());
        final int result = upvote(qid1, q1.getOwnerId());
        assertEquals(-1, result);

        listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        int newVotes1 = listLecture1after.get(0).getVotes();
        int newVotes2 = listLecture2after.get(0).getVotes();

        assertEquals(oldVotes1, newVotes1);
        assertEquals(oldVotes2, newVotes2);
    }

    @Test
    void editSuccessfulTest() throws Exception {
        long qid1 = Long.parseLong(postQuestions(q1Json));

        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", qid1);
        node.put("modkey", lectureEntity1.getModkey().toString());
        node.put("text", "this is the new text");
        node.put("uid", 42);
        String json = node.toString();

        int result = editQuestion(json);
        assertEquals(0, result);

        QuestionEntity question1after = getQuestions(lectureEntity1.getUuid().toString()).get(0);

        assertNotNull(question1after);
        assertEquals(question1after.getText(), "this is the new text");
        assertEquals(0, question1after.getOwnerId());       //check that owner id is not exposed
    }

    @Test
    void editUnsuccessfulUnauthorizedTest() throws Exception {
        long qid1 = Long.parseLong(postQuestions(q1Json));

        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", qid1);
        node.put("modkey", lectureEntity2.getModkey().toString());
        node.put("text", "this is the new text");
        node.put("uid", 12);
        String json = node.toString();

        int result = editQuestion(json);
        assertEquals(-1, result);
        QuestionEntity question1after = getQuestions(lectureEntity1.getUuid().toString()).get(0);
        assertNotNull(question1after);
        assertEquals(question1after.getText(),
                "What would you do if a seagull entered in your house?");
        assertNotEquals(question1after.getOwnerId(), 12);
    }

    @Test
    void editUnsuccessfulBadRequestTest() throws Exception {
        long qid1 = Long.parseLong(postQuestions(q1Json));

        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", qid1);
        node.put("modkey", "oh yes i know this uuid");
        node.put("text", "this is the new text");
        node.put("uid", 12);
        String json = node.toString();

        String result = this.mockMvc
                .perform(put("/api/question/edit")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .characterEncoding("utf-8"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
        assertEquals("Invalid UUID", result);

        QuestionEntity question1after = getQuestions(lectureEntity1.getUuid().toString()).get(0);
        assertNotNull(question1after);
        assertEquals(question1after.getText(),
                "What would you do if a seagull entered in your house?");
        assertNotEquals(question1after.getOwnerId(), 12);
    }

    @Test
    void answerSuccessfulTest() throws Exception {
        final long qid1 = Long.parseLong(postQuestions(q1Json));
        postQuestions(q2Json);

        List<QuestionEntity> listLecture1after;
        List<QuestionEntity> listLecture2after;

        final int result = answer(qid1, lectureEntity1.getModkey().toString());
        assertEquals(0, result);

        listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        boolean q1status = listLecture1after.get(0).isAnswered();
        boolean q2status = listLecture2after.get(0).isAnswered();

        assertTrue(q1status);
        assertFalse(q2status);
    }

    @Test
    void answerUnuccessfulTest() throws Exception {
        final long qid1 = Long.parseLong(postQuestions(q1Json));
        postQuestions(q2Json);

        List<QuestionEntity> listLecture1after;
        List<QuestionEntity> listLecture2after;

        final int result = answer(qid1, lectureEntity2.getModkey().toString());
        assertEquals(-1, result);

        listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        boolean q1status = listLecture1after.get(0).isAnswered();
        boolean q2status = listLecture2after.get(0).isAnswered();

        assertFalse(q1status);
        assertFalse(q2status);
    }

    @Test
    void answerUnuccessfulInvalidModkeyTest() throws Exception {
        final long qid1 = Long.parseLong(postQuestions(q1Json));
        postQuestions(q2Json);

        List<QuestionEntity> listLecture1after;
        List<QuestionEntity> listLecture2after;

        String resultString = this.mockMvc.perform(
                put("/api/question/answer/" + qid1 + "/" + "modkey"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
        assertEquals("Invalid UUID", resultString);

        listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        boolean q1status = listLecture1after.get(0).isAnswered();
        boolean q2status = listLecture2after.get(0).isAnswered();

        assertFalse(q1status);
        assertFalse(q2status);
    }
}