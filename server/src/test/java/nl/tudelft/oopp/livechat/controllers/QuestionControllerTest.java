package nl.tudelft.oopp.livechat.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.sql.Timestamp;
import java.util.List;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserQuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import nl.tudelft.oopp.livechat.services.LectureService;
import nl.tudelft.oopp.livechat.services.QuestionService;
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
    private LectureService lectureService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserQuestionRepository userQuestionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Timestamp time = new Timestamp(System.currentTimeMillis());
    private static final long uid1 = 1268346912741204312L;
    private static final long uid2 = 8976889685345625524L;

    private static LectureEntity lectureEntity1;
    private static LectureEntity lectureEntity2;

    private static QuestionEntity q1;
    private static QuestionEntity q2;

    private static UserEntity user1;
    private static UserEntity user2;

    /*/**
     * A helper method to create a JSON string representing the lecture.
     * @param creatorName the creator name
     * @param startTime the start time
     * @return the JSON string representing the lecture
     */
    /*private String createJson(String creatorName, Timestamp startTime) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("creatorName", creatorName);
        node.put("startTime", String.valueOf(startTime));
        return node.toString();

        q1Json = objectMapper.writeValueAsString(q1);
        JsonNode node1 = objectMapper.readTree(q1Json);
        node1 = ((ObjectNode) node1).put("ownerId", q1.getOwnerId());
        q1Json = node1.toString();
    }*/

    /**
     * A helper method to convert question to json.
     * @param q question object
     * @return the json representing the object
     * @throws JsonProcessingException if something goes wrong
     */
    private String createQuestionJson(QuestionEntity q) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(q);
        JsonNode node = objectMapper.readTree(json);
        node = ((ObjectNode) node).put("ownerId", q.getOwnerId());
        json = node.toString();
        return json;
    }


    @BeforeAll
    public static void setup() {
        lectureEntity1 = new LectureEntity("How to get 10 in OOPP",
                "Jegor", time);
        lectureEntity2 = new LectureEntity("How to get 100 in OOPP",
                "Jegorka", time);

        user1 = new UserEntity(1268346912741204312L, "Ivo", new Timestamp(
                System.currentTimeMillis()), true,
                "192.168.1.1", lectureEntity1.getUuid());
        user2 = new UserEntity(8976889685345625524L, "Stefan", new Timestamp(
                System.currentTimeMillis()), false,
                "192.185.7.3", lectureEntity2.getUuid());

        q1 = new QuestionEntity(lectureEntity1.getUuid(),
                "What would you do if a seagull entered in your house?",
                new Timestamp(System.currentTimeMillis()), uid1);
        q2 = new QuestionEntity(lectureEntity2.getUuid(),
                "What would you do if a pelican entered in your house?",
                new Timestamp(System.currentTimeMillis()), uid2);
    }

    @BeforeEach
    void setUp() {
        lectureRepository.save(lectureEntity1);
        lectureRepository.save(lectureEntity2);

        q1 = questionRepository.save(q1);
        q2 = questionRepository.save(q2);

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    void cleanUp() {
        if (questionRepository.findById(q1.getId()).isPresent()) {
            questionRepository.deleteById(q1.getId());
        }
        if (questionRepository.findById(q2.getId()).isPresent()) {
            questionRepository.deleteById(q2.getId());
        }
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
        List<QuestionEntity> list = objectMapper.readValue(listLectureString,
                new TypeReference<>(){});
        int pos = 0;
        for (String q : listLectureString.split("(},\\{)|(}])")) {
            for (String prop : q.split(",")) {
                if (prop.startsWith("\"id\"")) {
                    long id = Long.parseLong(prop.split(":")[1]);
                    list.get(pos++).setId(id);
                    break;
                }
            }
        }
        return list;
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
    int editQuestion(String bodyJson) throws Exception {
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
     * A method to answer a question.
     * @param qid id of the question
     * @param modkey the modkey
     * @return 0 if successful, otherwise -1
     * @throws Exception if something goes wrong
     */
    int answer(long qid, String modkey) throws Exception {
        String result = this.mockMvc.perform(put("/api/question/answer/" + qid + "/" + modkey)
                .contentType(APPLICATION_JSON)
                .content("This is definitely a question answer dude")
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return Integer.parseInt(result);
    }

    @Test
    void askQuestionSuccessfulTest() throws Exception {
        String qid1string = postQuestions(createQuestionJson(q1));
        long qid = Long.parseLong(qid1string);
        assertTrue(qid > 0);
        assertNotEquals(q1.getId(), qid);

        //delete the newly created question to make the tests independent
        questionRepository.deleteById(qid);
    }

    @Test
    void askQuestionSameIdTest() throws Exception {
        //delete the questions that were set in the beforeEach
        questionRepository.deleteById(q1.getId());
        questionRepository.deleteById(q2.getId());

        //post a new question
        long qid = Long.parseLong(postQuestions(createQuestionJson(q1)));

        //post a question with the same id as the newly created question
        q2.setId(qid);
        String result = postQuestions(createQuestionJson(q2));
        long qid1 = Long.parseLong(result);
        //check if still successful, because JSON does not deserialize the id
        assertTrue(qid1 > 0);

        //check if there are no questions with the id of q2
        QuestionEntity q = questionRepository.findById(q1.getId()).orElse(null);
        assertNull(q);
        //check that even though q2 and the new question have different ids,
        // they are actually the same question (in terms of text, lecture id etc)
        q = questionRepository.findById(qid1).orElse(null);
        assertNotNull(q);
        assertNotEquals(q, q1);

        assertEquals(q.getLectureId(), q2.getLectureId());
        assertEquals(q.getText(), q2.getText());

        //delete the newly created questions to make the tests independent
        questionRepository.deleteById(qid);
        questionRepository.deleteById(qid1);
    }

    @Test
    public void askQuestionFailingJSONTest() throws Exception {
        String response = this.mockMvc
                .perform(post("/api/question/ask")
                        .contentType(APPLICATION_JSON)
                        .content("not a question")
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals("Don't do this", response);
    }

    @Test
    void fetchQuestionsTest() throws Exception {
        List<QuestionEntity> listLecture1 = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2 = getQuestions(lectureEntity2.getUuid().toString());

        assertEquals(1, listLecture1.size());
        assertEquals(1, listLecture2.size());

        assertEquals(q1.getId(), listLecture1.get(0).getId());
        assertEquals(q2.getId(), listLecture2.get(0).getId());
    }

    @Test
    void fetchQuestionsFakeIdTest() throws Exception {
        this.mockMvc
                .perform(get("/api/question/fetch?lid=" + "something_wrong"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void fetchQuestionsNoQuestionsTest() throws Exception {
        questionRepository.deleteById(q1.getId());
        questionRepository.deleteById(q2.getId());

        List<QuestionEntity> listLecture = getQuestions(lectureEntity1.getUuid().toString());
        assertEquals(0, listLecture.size());
    }

    @Test
    void deleteQuestionSuccessfulTest() throws Exception {
        int result = deleteQuestion("/api/question/delete?qid=" + q1.getId()
                                                + "&uid=" + q1.getOwnerId());
        assertEquals(0, result);

        List<QuestionEntity> listLecture1after =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        List<QuestionEntity> listLecture2after =
                questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        assertEquals(0, listLecture1after.size());
        assertEquals(1, listLecture2after.size());
    }

    @Test
    void deleteQuestionUnsuccessfulTest() throws Exception {
        int result = deleteQuestion("/api/question/delete?qid=" + q1.getId()
                                                + "&uid=" + q2.getOwnerId());
        assertEquals(-1, result);

        List<QuestionEntity> listLecture1after =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        List<QuestionEntity> listLecture2after =
                questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        assertEquals(1, listLecture1after.size());
        assertEquals(1, listLecture2after.size());
    }

    @Test
    void modDeleteSuccessfulTest() throws Exception {
        int result = deleteQuestion("/api/question/moderator/delete?qid="
                + q1.getId() + "&modkey=" + lectureEntity1.getModkey().toString());
        assertEquals(0, result);

        List<QuestionEntity> listLecture1after =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        List<QuestionEntity> listLecture2after =
                questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        assertEquals(0, listLecture1after.size());
        assertEquals(1, listLecture2after.size());
    }

    @Test
    void modDeleteUnsuccessfulTest() throws Exception {
        final int result = deleteQuestion("/api/question/moderator/delete?qid=" + q1.getId()
                + "&modkey=" + lectureEntity2.getModkey().toString());
        assertEquals(-1, result);

        List<QuestionEntity> listLecture1after = getQuestions(lectureEntity1.getUuid().toString());
        List<QuestionEntity> listLecture2after = getQuestions(lectureEntity2.getUuid().toString());

        assertEquals(1, listLecture1after.size());
        assertEquals(1, listLecture2after.size());
    }

    @Test
    void upvoteSuccessfulTest() throws Exception {
        List<QuestionEntity> listLecture1 =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        List<QuestionEntity> listLecture2 =
                questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        final int oldVotes1 = listLecture1.get(0).getVotes();
        final int oldVotes2 = listLecture2.get(0).getVotes();

        final int result = upvote(q1.getId(), q1.getOwnerId());
        assertEquals(0, result);

        listLecture1 = questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        listLecture2 = questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        int newVotes1 = listLecture1.get(0).getVotes();
        int newVotes2 = listLecture2.get(0).getVotes();

        assertEquals(oldVotes1 + 1, newVotes1);
        assertEquals(oldVotes2, newVotes2);
    }

    @Test
    void unvoteSuccessfulTest() throws Exception {
        List<QuestionEntity> listLecture1 =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        List<QuestionEntity> listLecture2 =
                questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        final int oldVotes1 = listLecture1.get(0).getVotes();
        final int oldVotes2 = listLecture2.get(0).getVotes();

        upvote(q1.getId(), q1.getOwnerId());
        final int result = upvote(q1.getId(), q1.getOwnerId());
        assertEquals(0, result);

        listLecture1 = questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        listLecture2 = questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        int newVotes1 = listLecture1.get(0).getVotes();
        int newVotes2 = listLecture2.get(0).getVotes();

        assertEquals(oldVotes1, newVotes1);
        assertEquals(oldVotes2, newVotes2);
    }

    @Test
    void upvoteUnsuccessfulTest() throws Exception {
        long qid11 = -1;

        List<QuestionEntity> listLecture1 =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        List<QuestionEntity> listLecture2 =
                questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        final int oldVotes1 = listLecture1.get(0).getVotes();
        final int oldVotes2 = listLecture2.get(0).getVotes();

        final int result = upvote(qid11, q1.getOwnerId());
        assertEquals(-1, result);

        listLecture1 = questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        listLecture2 = questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        int newVotes1 = listLecture1.get(0).getVotes();
        int newVotes2 = listLecture2.get(0).getVotes();

        assertEquals(oldVotes1, newVotes1);
        assertEquals(oldVotes2, newVotes2);
    }

    @Test
    void editSuccessfulTest() throws Exception {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", q1.getId());
        node.put("modkey", lectureEntity1.getModkey().toString());
        node.put("text", "this is the new text");
        node.put("uid", uid1);
        String json = node.toString();

        int result = editQuestion(json);
        assertEquals(0, result);

        QuestionEntity question1after = getQuestions(lectureEntity1.getUuid().toString()).get(0);

        assertNotNull(question1after);
        assertEquals(question1after.getText(), "this is the new text");
        assertTrue(question1after.isEdited());
        assertEquals(0, question1after.getOwnerId());       //check that owner id is not exposed
    }

    @Test
    void editUnsuccessfulUnauthorizedTest() throws Exception {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", q1.getId());
        node.put("modkey", lectureEntity2.getModkey().toString());
        node.put("text", "this is the new text");
        node.put("uid", uid2);
        String json = node.toString();

        int result = editQuestion(json);
        assertEquals(-1, result);

        QuestionEntity question1after = getQuestions(lectureEntity1.getUuid().toString()).get(0);
        assertNotNull(question1after);
        assertEquals(question1after.getText(),
                "What would you do if a seagull entered in your house?");
        assertFalse(question1after.isEdited());
        assertNotEquals(question1after.getOwnerId(), uid2);
    }

    @Test
    void editUnsuccessfulBadRequestTest() throws Exception {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", q1.getId());
        node.put("modkey", "oh yes i know this uuid");
        node.put("text", "this is the new text");
        node.put("uid", uid2);
        String json = node.toString();

        String result = this.mockMvc
                .perform(put("/api/question/edit")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .characterEncoding("utf-8"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
        assertEquals("Don't do this", result);

        QuestionEntity question1after = getQuestions(lectureEntity1.getUuid().toString()).get(0);
        assertNotNull(question1after);
        assertFalse(question1after.isEdited());
        assertEquals(question1after.getText(),
                "What would you do if a seagull entered in your house?");
        assertNotEquals(question1after.getOwnerId(), uid2);
    }

    @Test
    void answerSuccessfulTest() throws Exception {
        final int result = answer(q1.getId(), lectureEntity1.getModkey().toString());
        assertEquals(0, result);

        List<QuestionEntity> listLecture1 =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        List<QuestionEntity> listLecture2 =
                questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        assertTrue(listLecture1.get(0).isAnswered());
        assertFalse(listLecture2.get(0).isAnswered());
    }

    @Test
    void answerUnsuccessfulTest() throws Exception {
        final int result = answer(q1.getId(), lectureEntity2.getModkey().toString());
        assertEquals(-1, result);

        List<QuestionEntity> listLecture1 =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        List<QuestionEntity> listLecture2 =
                questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        assertFalse(listLecture1.get(0).isAnswered());
        assertFalse(listLecture2.get(0).isAnswered());
    }

    @Test
    void answerUnsuccessfulInvalidModkeyTest() throws Exception {
        String resultString = this.mockMvc.perform(
                put("/api/question/answer/" + q1.getId() + "/" + "modkey"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
        assertEquals("Don't do this", resultString);

        List<QuestionEntity> listLecture1 =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());
        List<QuestionEntity> listLecture2 =
                questionRepository.findAllByLectureId(lectureEntity2.getUuid());

        assertFalse(listLecture1.get(0).isAnswered());
        assertFalse(listLecture2.get(0).isAnswered());
    }
}