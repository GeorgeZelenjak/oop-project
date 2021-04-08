package nl.tudelft.oopp.livechat.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.AsyncContext;


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

        lectureEntity1.setFrequency(0);
        lectureEntity2 = new LectureEntity("How to get 100 in OOPP",
                "Jegorka", time);
        lectureEntity2.setFrequency(0);
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
    String postQuestion(String question) throws Exception {
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
    List<QuestionEntity> getQuestions(String lectureId, boolean firstTime) throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(get("/api/question/fetch?lid=" + lectureId + "&firstTime=" + firstTime))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();
        AsyncContext context = mvcResult.getRequest().getAsyncContext();
        if (context == null) return  null;
        context.setTimeout(10000);

        String listLectureString = this.mockMvc
                .perform(asyncDispatch(mvcResult))
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
     * @return 0 if successful
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
     * @return 0 if successful
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
     * @return 0 if successful
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
     * @param qid the id of the question
     * @param modkey the moderator key
     * @return 0 if successful
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

    /**
     * A method to set the status of a question.
     * @param qid the id of the question
     * @param modkey the moderator key
     * @param uid the id of the user
     * @return 0 if successful
     * @throws Exception if something goes wrong
     */
    int setStatus(long qid, UUID modkey, long uid) throws Exception {
        String result = this.mockMvc.perform(put("/api/question/status/"
                    + qid + "/" + uid + "/" + modkey)
                .contentType(APPLICATION_JSON)
                .content("editing")
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return Integer.parseInt(result);
    }

    @Test
    void askQuestionSuccessfulTest() throws Exception {
        String qid1string = postQuestion(createQuestionJson(q1));
        long qid = Long.parseLong(qid1string);
        assertTrue(qid > 0);
        assertNotEquals(q1.getId(), qid);

        //delete the newly created question to make the tests independent
        questionRepository.deleteById(qid);
    }

    @Test
    void askQuestionUserBannedTest() throws Exception {
        questionRepository.deleteById(q2.getId());

        String result = this.mockMvc.perform(post("/api/question/ask")
                        .contentType(APPLICATION_JSON)
                        .content(createQuestionJson(q2))
                        .characterEncoding("utf-8"))
                        .andExpect(status().isForbidden())
                        .andReturn().getResponse().getErrorMessage();
        assertEquals("This user is banned", result);

        //check if there are no questions with the id of q2
        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNull(q);
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
    public void askQuestionTooEarlyExceptionTest() throws Exception {
        lectureEntity1.setFrequency(300);
        lectureRepository.save(lectureEntity1);
        user1.setLastQuestion(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user1);

        String response = this.mockMvc
                .perform(post("/api/question/ask")
                        .contentType(APPLICATION_JSON)
                        .content(createQuestionJson(q1))
                        .characterEncoding("utf-8"))
                .andExpect(status().isTooEarly())
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
        assertTrue(response.contains("Not enough time has passed between questions"));

        lectureEntity1.setFrequency(0);
        lectureRepository.save(lectureEntity1);
        user1.setLastQuestion(null);
        userRepository.save(user1);
    }

    @Test
    void fetchQuestionsTest() throws Exception {
        user2.setAllowed(true);
        userRepository.save(user2);

        List<QuestionEntity> listLecture1 = getQuestions(lectureEntity1.getUuid().toString(), true);
        List<QuestionEntity> listLecture2 = getQuestions(lectureEntity2.getUuid().toString(), true);

        assertEquals(1, listLecture1.size());
        assertEquals(1, listLecture2.size());

        assertEquals(q1.getId(), listLecture1.get(0).getId());
        assertEquals(q2.getId(), listLecture2.get(0).getId());


        user2.setAllowed(false);
        userRepository.save(user2);
    }

    @Test
    void fetchQuestionsNotFirstTimeTest() throws Exception {
        /*LectureEntity lectureEntity = new LectureEntity();
        lectureRepository.save(lectureEntity);

        MvcResult mvcResult = mockMvc.perform(get("/api/question/fetch?lid="
                + lectureEntity.getUuid() + "&firstTime=" + false))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isRequestTimeout())
                .andExpect(content().string("Request timeout occurred."));

        lectureRepository.deleteById(lectureEntity.getUuid());*/
    }

    @Test
    void fetchQuestionsFakeIdTest() throws Exception {
        this.mockMvc.perform(get("/api/question/fetch?lid=" + "something_wrong"))
                    .andExpect(status().is4xxClientError());
    }

    @Test
    void fetchQuestionsNoLectureTest() throws Exception {
        String result = this.mockMvc.perform(get("/api/question/fetch?lid="
                + UUID.randomUUID() + "&firstTime=" + true)).andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Lecture not found", result);
    }

    @Test
    void fetchQuestionsNoQuestionsTest() throws Exception {
        questionRepository.deleteById(q1.getId());
        questionRepository.deleteById(q2.getId());

        List<QuestionEntity> listLecture = getQuestions(lectureEntity1.getUuid().toString(), false);
        assertEquals(0, listLecture.size());
    }

    @Test
    void deleteQuestionSuccessfulTest() throws Exception {
        int result = deleteQuestion("/api/question/delete?qid=" + q1.getId()
                                                + "&uid=" + q1.getOwnerId());
        assertEquals(0, result);

        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        assertEquals(0, listLecture.size());
    }

    @Test
    void deleteQuestionUnsuccessfulTest() throws Exception {
        String result = this.mockMvc.perform(delete("/api/question/delete?qid=" + q1.getId()
                + "&uid=" + q2.getOwnerId())).andExpect(status().isForbidden())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Not allowed to delete this question", result);

        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        assertEquals(1, listLecture.size());
    }

    @Test
    void modDeleteSuccessfulTest() throws Exception {
        int result = deleteQuestion("/api/question/moderator/delete?qid="
                + q1.getId() + "&modkey=" + lectureEntity1.getModkey().toString());
        assertEquals(0, result);

        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        assertEquals(0, listLecture.size());
    }

    @Test
    void modDeleteUnsuccessfulTest() throws Exception {
        String result = this.mockMvc.perform(delete("/api/question/moderator/delete?qid="
                + q1.getId() + "&modkey=" + lectureEntity2.getModkey().toString()))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);

        List<QuestionEntity> listLecture = questionRepository
                .findAllByLectureId(lectureEntity1.getUuid());

        assertEquals(1, listLecture.size());
    }

    @Test
    void upvoteSuccessfulTest() throws Exception {
        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        final int oldVotes = listLecture.get(0).getVotes();

        int result = upvote(q1.getId(), q1.getOwnerId());
        assertEquals(0, result);

        listLecture = questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        int newVotes = listLecture.get(0).getVotes();

        assertEquals(oldVotes + 1, newVotes);
    }

    @Test
    void unvoteSuccessfulTest() throws Exception {
        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        final int oldVotes = listLecture.get(0).getVotes();

        upvote(q1.getId(), q1.getOwnerId());
        int result = upvote(q1.getId(), q1.getOwnerId());
        assertEquals(0, result);

        listLecture = questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        int newVotes = listLecture.get(0).getVotes();
        assertEquals(oldVotes, newVotes);
    }

    @Test
    void upvoteUnsuccessfulTest() throws Exception {
        userRepository.deleteById(uid1);
        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        final int oldVotes = listLecture.get(0).getVotes();

        String result = this.mockMvc.perform(put("/api/question/upvote?qid=" + q1.getId()
                + "&uid=" + q1.getOwnerId())).andExpect(status().isConflict())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This user is not registered", result);

        listLecture = questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        int newVotes = listLecture.get(0).getVotes();
        assertEquals(oldVotes, newVotes);

        userRepository.save(user1);
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

        QuestionEntity question1after = getQuestions(lectureEntity1.getUuid()
                .toString(), false).get(0);

        assertNotNull(question1after);
        assertEquals(question1after.getText(), "this is the new text");
        assertTrue(question1after.isEdited());
        //check that owner id is not exposed
        assertEquals(0, question1after.getOwnerId());
    }

    @Test
    void editUnsuccessfulUnauthorizedTest() throws Exception {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", q1.getId());
        node.put("modkey", lectureEntity2.getModkey().toString());
        node.put("text", "this is the new text");
        node.put("uid", uid2);
        String json = node.toString();

        String result = this.mockMvc
                .perform(put("/api/question/edit")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);

        QuestionEntity question1after = questionRepository
                .findAllByLectureId(lectureEntity1.getUuid()).get(0);
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
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals("UUID is not in the correct format", result);

        QuestionEntity question1after = questionRepository
                .findAllByLectureId(lectureEntity1.getUuid()).get(0);
        assertNotNull(question1after);
        assertFalse(question1after.isEdited());
        assertEquals(question1after.getText(),
                "What would you do if a seagull entered in your house?");
        assertNotEquals(question1after.getOwnerId(), uid2);
    }

    @Test
    void editUnsuccessfulNullPointerTest() throws Exception {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", q1.getId());
        node.put("modkey", lectureEntity1.getModkey().toString());
        node.put("uid", uid2);
        String jason = node.toString();

        String result = this.mockMvc
                .perform(put("/api/question/edit")
                        .contentType(APPLICATION_JSON)
                        .content(jason)
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals("Missing parameter", result);

        QuestionEntity question1after = questionRepository
                .findAllByLectureId(lectureEntity1.getUuid()).get(0);
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

        assertTrue(listLecture1.get(0).isAnswered());
    }

    @Test
    void answerUnsuccessfulTest() throws Exception {
        String result = this.mockMvc.perform(put("/api/question/answer/"
                + q1.getId() + "/" + lectureEntity2.getModkey().toString())
                .contentType(APPLICATION_JSON)
                .content("This is definitely a question answer dude")
                .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);

        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        assertFalse(listLecture.get(0).isAnswered());
    }

    @Test
    void answerUnsuccessfulInvalidModkeyTest() throws Exception {
        String resultString = this.mockMvc.perform(
                put("/api/question/answer/" + q1.getId() + "/" + "modkey"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals("UUID is not in the correct format", resultString);

        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        assertFalse(listLecture.get(0).isAnswered());
    }

    @Test
    void setStatusSuccessfulTest() throws Exception {
        int result = setStatus(q1.getId(), lectureEntity1.getModkey(),
                user1.getUid());
        assertEquals(0, result);

        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        assertEquals("editing", listLecture.get(0).getStatus());
        assertEquals(user1.getUid(), listLecture.get(0).getEditorId());
    }

    @Test
    void setStatusWrongModKeyTest() throws Exception {
        String result = this.mockMvc.perform(put("/api/question/status/"
                + q1.getId() + "/" + user1.getUid() + "/" + UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .content("answering")
                .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);

        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        assertEquals("new", listLecture.get(0).getStatus());
        assertEquals(0, listLecture.get(0).getEditorId());
    }

    @Test
    void setStatusQuestionAlreadyModifiedTest() throws Exception {
        q1.setEditorId(42);
        q1.setStatus("editing");
        questionRepository.save(q1);

        String result = this.mockMvc.perform(put("/api/question/status/"
                + q1.getId() + "/" + user1.getUid() + "/" + lectureEntity1.getUuid())
                .contentType(APPLICATION_JSON)
                .content("answering")
                .characterEncoding("utf-8"))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Another moderator is already handling this question,"
                + " if you are sure of what you are doing you can continue", result);

        List<QuestionEntity> listLecture =
                questionRepository.findAllByLectureId(lectureEntity1.getUuid());

        assertEquals("editing", listLecture.get(0).getStatus());
        assertEquals(42, listLecture.get(0).getEditorId());

        q1.setEditorId(0);
        q1.setStatus("new");
    }
}