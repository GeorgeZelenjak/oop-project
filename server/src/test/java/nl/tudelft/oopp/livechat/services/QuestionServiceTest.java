package nl.tudelft.oopp.livechat.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.entities.UserQuestionTable;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserQuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * Class for Question service tests.
 */
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class QuestionServiceTest {
    private static LectureEntity l1;
    private static LectureEntity l2;
    private static LectureEntity l3;
    private static QuestionEntity q1;
    private static QuestionEntity q2;
    private static QuestionEntity q3;

    private static UserEntity user1;
    private static final long uid1 = 1268346912741204312L;
    private static UserEntity user2;
    private static final long uid2 = 8976889685345625524L;
    private static UserEntity user3;
    private static final long uid3 = 5453625625625654245L;

    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    private static String longText;

    @Autowired
    LectureService lectureService;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserQuestionRepository userQuestionRepository;

    /**
     * Sets up a lecture with questions before each test.
     */
    @BeforeAll
    public static void setup() {
        l1 = new LectureEntity("Lecture 1", "Ivo van Kreveld", time);
        l1.setFrequency(0);

        l2 = new LectureEntity("Lecture 2", "Stefan Hugtenburg", time);
        l2.setFrequency(0);

        l3 = new LectureEntity("Lecture 3", "Joana Gonçalves", time);
        l3.setFrequency(0);

        user1 = new UserEntity(uid1, "Koen", new Timestamp(
                System.currentTimeMillis()), true,
                "192.168.1.1", l1.getUuid());

        user2 = new UserEntity(uid2, "Otto", new Timestamp(
                System.currentTimeMillis()), false,
                "192.185.7.3", l2.getUuid());

        user3 = new UserEntity(uid3, "Taico", new Timestamp(
                System.currentTimeMillis()), true,
                "122.162.4.8", l3.getUuid());

        longText = "a".repeat(2001);

        q1 = new QuestionEntity(l1.getUuid(), "name?",
                new Timestamp(System.currentTimeMillis()), uid1);
        q2 = new QuestionEntity(l2.getUuid(), "How to get 10 for testing?",
                new Timestamp(System.currentTimeMillis()), uid2);
        q3 = new QuestionEntity(l3.getUuid(), "1 OR 1=1; DROP DATABASE;",
                new Timestamp(System.currentTimeMillis()), uid3);
    }

    @BeforeEach
    public void setUp() {
        lectureRepository.deleteAll();
        userRepository.deleteAll();
        questionRepository.deleteAll();

        lectureRepository.save(l1);
        userRepository.save(user1);
        lectureRepository.save(l2);
        userRepository.save(user2);

        questionRepository.save(q1);
        questionRepository.save(q2);
    }

    @AfterEach
    public void clean() {
        lectureRepository.deleteById(l1.getUuid());
        lectureRepository.deleteById(l2.getUuid());

        userRepository.deleteById(uid1);
        userRepository.deleteById(uid2);

        if (questionRepository.findById(q1.getId()).isPresent()) {
            questionRepository.deleteById(q1.getId());
        }
        if (questionRepository.findById(q2.getId()).isPresent()) {
            questionRepository.deleteById(q2.getId());
        }
    }

    @Test
    void constructorTest() {
        assertNotNull(questionService);
    }

    /**
     * Tests related to newQuestionEntity method.
     */
    @Test
    void newQuestionEntityTest() throws Exception {
        questionRepository.deleteById(q1.getId());

        long result = questionService.newQuestionEntity(q1);
        assertTrue(result > 0);
        assertTrue(questionRepository.findById(result).isPresent());
    }

    @Test
    void newQuestionEntityQuestionIsAskedTest() {
        assertThrows(QuestionAlreadyExistsException.class, () ->
                questionService.newQuestionEntity(q1));
    }

    @Test
    void newQuestionEntityLectureDoesNotExistTest() {
        questionRepository.deleteById(q2.getId());

        lectureRepository.deleteById(l2.getUuid());

        assertThrows(LectureNotFoundException.class, () -> questionService.newQuestionEntity(q2));

        lectureRepository.save(l2);
    }

    @Test
    void newQuestionEntityLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        questionRepository.deleteById(q2.getId());

        assertThrows(LectureClosedException.class, () -> questionService.newQuestionEntity(q2));

        l2.reOpen();
        lectureRepository.save(l2);
    }

    @Test
    void newQuestionEntityTooLongTextTest() {
        userRepository.save(user3);
        lectureRepository.save(l3);
        q3.setText(longText);

        assertThrows(QuestionNotAskedException.class, () -> questionService.newQuestionEntity(q3));

        q3.setText("1 OR 1=1; DROP DATABASE;");
        userRepository.deleteById(uid3);
        lectureRepository.deleteById(l3.getUuid());
    }

    @Test
    void newQuestionEntityOwnerNotRegisteredTest() {
        questionRepository.deleteById(q2.getId());
        userRepository.deleteById(uid2);

        assertThrows(UserNotRegisteredException.class, () -> questionService.newQuestionEntity(q2));

        userRepository.save(user2);
    }

    @Test
    void newQuestionEntityOwnerNotAllowedTest() {
        lectureRepository.save(l3);
        user3.setAllowed(false);
        userRepository.save(user3);

        assertThrows(UserBannedException.class, () -> questionService.newQuestionEntity(q3));

        userRepository.deleteById(uid3);
        user3.setAllowed(true);
        lectureRepository.deleteById(l3.getUuid());
    }

    @Test
    void newQuestionEntityTooFrequentlyTest() throws Exception {
        userRepository.save(user3);
        lectureRepository.save(l3);

        QuestionEntity q = new QuestionEntity(l3.getUuid(), "name???",
                new Timestamp(System.currentTimeMillis()), uid3);
        questionService.newQuestionEntity(q);

        l3.setFrequency(3);
        lectureRepository.save(l3);
        //test that one cannot fake the question time
        QuestionEntity qq = new QuestionEntity(l3.getUuid(), "name???",
                new Timestamp(0), uid3);

        assertThrows(QuestionFrequencyTooFastException.class, () ->
                questionService.newQuestionEntity(qq));

        questionRepository.deleteById(q.getId());
        l3.setFrequency(0);
        lectureRepository.deleteById(l3.getUuid());
        userRepository.deleteById(uid3);
    }

    @Test
    void newQuestionEntityFrequencySuccessfulTest() throws Exception {
        userRepository.save(user3);
        lectureRepository.save(l3);

        QuestionEntity q = new QuestionEntity(l3.getUuid(), "name???",
                new Timestamp(System.currentTimeMillis()), uid3);
        questionService.newQuestionEntity(q);
        l3.setFrequency(3);
        lectureRepository.save(l3);

        Thread.sleep(3500);

        QuestionEntity qq = new QuestionEntity(l3.getUuid(), "name???",
                new Timestamp(System.currentTimeMillis()), uid3);

        assertTrue(questionService.newQuestionEntity(qq) > 0);

        questionRepository.deleteById(q.getId());
        questionRepository.deleteById(qq.getId());
        l3.setFrequency(0);
        lectureRepository.deleteById(l3.getUuid());
        userRepository.deleteById(uid3);
    }

    @Test
    void newQuestionLectureNotStartedTest() {
        l2.setStartTime(new Timestamp(System.currentTimeMillis() + 0xFFFFFFFFFL));
        lectureRepository.save(l2);
        q3.setLectureId(l2.getUuid());
        q3.setOwnerId(uid2);

        assertThrows(LectureNotStartedException.class, () -> questionService.newQuestionEntity(q3));

        q3.setOwnerId(uid3);
        q3.setLectureId(l3.getUuid());
        l2.setStartTime(l1.getStartTime());
    }

    /**
     * Tests related to getQuestionsByLectureId method.
     */
    @Test
    void getQuestionsByLectureIdSuccessfulTest() {
        List<QuestionEntity> qs = questionService
                .getQuestionsByLectureId(l1.getUuid());
        assertEquals(1, qs.size());
        assertEquals(q1, qs.get(0));
    }

    @Test
    void getQuestionsByLectureIdNoQuestionsTest() {
        questionRepository.deleteById(q2.getId());

        List<QuestionEntity> qs = questionService.getQuestionsByLectureId(l2.getUuid());
        assertEquals(0, qs.size());
    }

    @Test
    void getQuestionsByLectureIdNoLectureTest() {
        userRepository.save(user3);
        q3.setLectureId(UUID.randomUUID());
        questionRepository.save(q3);

        List<QuestionEntity> qs = questionService
                .getQuestionsByLectureId(l3.getUuid());
        assertEquals(0, qs.size());

        userRepository.deleteById(uid3);
        questionRepository.deleteById(q3.getId());
        q3.setLectureId(l3.getUuid());
    }

    /**
     * Tests related to deleteQuestion method.
     */
    @Test
    void deleteQuestionNoQuestionTest() {
        userRepository.save(user3);
        lectureRepository.save(l3);

        assertThrows(QuestionNotFoundException.class, () ->
                questionService.deleteQuestion(q3.getId(), uid3));

        userRepository.deleteById(uid3);
        lectureRepository.deleteById(l3.getUuid());
    }

    @Test
    void deleteQuestionWrongUidTest() {
        assertThrows(QuestionWrongOwnerIdException.class, () ->
                questionService.deleteQuestion(q1.getId(), uid2));
        assertTrue(questionRepository.findById(q1.getId()).isPresent());
    }

    @Test
    void deleteQuestionNoLectureTest() {
        lectureRepository.deleteById(l2.getUuid());

        assertThrows(LectureNotFoundException.class, () ->
                questionService.deleteQuestion(q2.getId(), uid2));
        assertTrue(questionRepository.findById(q2.getId()).isPresent());

        lectureRepository.save(l2);
    }

    @Test
    void deleteQuestionLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        questionRepository.save(q2);

        assertThrows(LectureClosedException.class, () ->
                questionService.deleteQuestion(q2.getId(), uid2));
        assertTrue(questionRepository.findById(q2.getId()).isPresent());

        l2.reOpen();
        lectureRepository.save(l2);
    }

    @Test
    void deleteQuestionUserNotRegisteredTest() {
        lectureRepository.save(l3);
        questionRepository.save(q3);

        assertThrows(UserNotRegisteredException.class, () ->
                questionService.deleteQuestion(q3.getId(), uid3));
        assertTrue(questionRepository.findById(q3.getId()).isPresent());

        lectureRepository.deleteById(l3.getUuid());
        questionRepository.deleteById(q3.getId());
    }

    @Test
    void deleteQuestionSuccessfulTest() throws Exception {
        userQuestionRepository.save(new UserQuestionTable(uid2, q2.getId()));
        assertEquals(0, questionService.deleteQuestion(q2.getId(), uid2));
        assertTrue(questionRepository.findById(q2.getId()).isEmpty());
        assertTrue(userQuestionRepository.getAllByQuestionId(q2.getId()).isEmpty());
    }

    /**
     * Tests related to deleteModeratorQuestion method.
     */

    @Test
    void deleteModeratorQuestionNoQuestionTest() {
        assertThrows(QuestionNotFoundException.class, () ->
                questionService.deleteModeratorQuestion(q3.getId(), l3.getModkey()));
    }

    @Test
    void deleteModeratorQuestionNoLectureTest() {
        questionRepository.save(q3);

        assertThrows(LectureNotFoundException.class, () ->
                questionService.deleteModeratorQuestion(q3.getId(), l3.getModkey()));
        assertTrue(questionRepository.findById(q3.getId()).isPresent());

        questionRepository.deleteById(q3.getId());
    }

    @Test
    void deleteModeratorQuestionWrongModkeyTest() {
        assertThrows(InvalidModkeyException.class, () ->
                questionService.deleteModeratorQuestion(q2.getId(), l1.getModkey()));
        assertTrue(questionRepository.findById(q2.getId()).isPresent());
    }

    @Test
    void deleteModeratorQuestionSuccessfulTest() throws Exception {
        userQuestionRepository.save(new UserQuestionTable(uid2, q2.getId()));

        assertEquals(0, questionService.deleteModeratorQuestion(q2.getId(), l2.getModkey()));
        assertTrue(questionRepository.findById(q2.getId()).isEmpty());
        assertTrue(userQuestionRepository.getAllByQuestionId(q2.getId()).isEmpty());
    }

    /**
     * Tests related to editQuestion method.
     */
    @Test
    void editQuestionNoQuestionTest() {
        assertThrows(QuestionNotFoundException.class, () ->
                questionService.editQuestion(q3.getId(), l3.getModkey(), "bla bla", uid1));
    }

    @Test
    void editQuestionNoLectureTest() {
        questionRepository.save(q3);

        assertThrows(LectureNotFoundException.class, () ->
                questionService.editQuestion(q3.getId(), l3.getModkey(), "foo", uid1));

        QuestionEntity q = questionRepository.findById(q3.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("1 OR 1=1; DROP DATABASE;", q.getText());
        assertFalse(q.isEdited());

        questionRepository.deleteById(q3.getId());
    }

    @Test
    void editQuestionWrongModkeyTest() {
        assertThrows(InvalidModkeyException.class, () ->
                questionService.editQuestion(q2.getId(), l1.getModkey(), "foo", uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());
        assertFalse(q.isEdited());
    }

    @Test
    void editQuestionTooLongTextTest() {
        assertThrows(QuestionNotModifiedException.class, () ->
                questionService.editQuestion(q2.getId(), l2.getModkey(), longText, uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());
        assertFalse(q.isEdited());
    }

    @Test
    void editQuestionNewOwnerNotRegisteredTest() {
        userRepository.deleteById(uid2);

        assertThrows(UserNotRegisteredException.class, () ->
                questionService.editQuestion(q2.getId(), l2.getModkey(), "bar", uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());
        assertFalse(q.isEdited());

        userRepository.save(user2);
    }

    @Test
    void editQuestionSuccessfulTest() throws Exception {
        assertEquals(0, questionService.editQuestion(q2.getId(), l2.getModkey(), "bar", uid1));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("bar", q.getText());
        assertEquals(uid1, q.getOwnerId());
        assertTrue(q.isEdited());

        q2.setOwnerId(uid2);
        q2.setText("How to get 10 for testing?");
    }

    /**
     * Tests related to upvote method.
     */
    @Test
    void upvoteNoQuestionTest() {
        userRepository.save(user3);

        assertThrows(QuestionNotFoundException.class, () ->
                questionService.upvote(q3.getId(), uid3));

        userRepository.deleteById(uid3);
    }

    @Test
    void upvoteNoLectureTest() {
        questionRepository.save(q3);

        assertThrows(LectureNotFoundException.class, () ->
                questionService.upvote(q3.getId(), uid3));

        QuestionEntity q = questionRepository.findById(q3.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(0, q.getVotes());

        questionRepository.deleteById(q3.getId());
    }

    @Test
    void upvoteLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        questionRepository.save(q2);

        assertThrows(LectureClosedException.class, () -> questionService.upvote(q2.getId(), uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(0, q.getVotes());

        l2.reOpen();
        lectureRepository.save(l2);
    }

    @Test
    void upvoteNotRegisteredTest() {
        userRepository.deleteById(uid2);
        assertThrows(UserNotRegisteredException.class, () ->
                questionService.upvote(q2.getId(), uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(0, q.getVotes());

        userRepository.save(user2);
    }

    @Test
    void upvoteTest() throws Exception {
        assertEquals(0, questionService.upvote(q2.getId(), uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(1, q.getVotes());

        List<UserQuestionTable> table = userQuestionRepository.getAllByQuestionId(q2.getId());
        UserQuestionTable match = new UserQuestionTable(uid2, q2.getId());
        assertTrue(table.contains(match));

        userQuestionRepository.deleteAllByQuestionId(q2.getId());
    }

    @Test
    void unvoteTest() throws Exception {
        questionService.upvote(q2.getId(), uid2);

        assertEquals(0, questionService.upvote(q2.getId(), uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(0, q.getVotes());

        List<UserQuestionTable> table = userQuestionRepository.getAllByQuestionId(q2.getId());
        UserQuestionTable match = new UserQuestionTable(uid2, q2.getId());
        assertFalse(table.contains(match));

        userQuestionRepository.deleteAllByQuestionId(q2.getId());
    }

    /**
     * Tests related to answer method.
     */
    @Test
    void answerNoQuestionTest() {
        lectureRepository.save(l3);

        assertThrows(QuestionNotFoundException.class, () ->
                questionService.answer(q3.getId(), l3.getModkey(), "42"));

        lectureRepository.deleteById(l3.getUuid());
    }

    @Test
    void answerNoLectureTest() {
        questionRepository.save(q3);

        assertThrows(LectureNotFoundException.class, () ->
                questionService.answer(q3.getId(), l3.getModkey(), "42"));

        QuestionEntity q = questionRepository.findById(q3.getId()).orElse(null);
        assertNotNull(q);
        assertNull(q.getAnswerText());
        assertNull(q.getAnswerTime());
        assertFalse(q.isAnswered());

        questionRepository.deleteById(q3.getId());
    }

    @Test
    void answerTooLongTextTest() {
        assertThrows(QuestionNotModifiedException.class, () ->
                questionService.answer(q2.getId(), l2.getModkey(), longText));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertNull(q.getAnswerText());
        assertNull(q.getAnswerTime());
        assertFalse(q.isAnswered());
    }

    @Test
    void answerWrongModKeyTest() {
        assertThrows(InvalidModkeyException.class, () ->
                questionService.answer(q2.getId(), l1.getModkey(), "42"));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertNull(q.getAnswerText());
        assertNull(q.getAnswerTime());
        assertFalse(q.isAnswered());
    }

    @Test
    void answerSuccessfulTest() throws Exception {
        assertEquals(0, questionService.answer(q2.getId(), l2.getModkey(), "42"));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("42", q.getAnswerText());
        assertNotNull(q.getAnswerTime());
        assertTrue(q.isAnswered());
    }

    @Test
    void answerSuccessfulNoAnswerTextTest() throws Exception {
        assertEquals(0, questionService.answer(q2.getId(), l2.getModkey(), ""));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("", q.getAnswerText());
        assertNotNull(q.getAnswerTime());
        assertTrue(q.isAnswered());
    }

    /**
     * Tests related to set status method.
     */

    @Test
    void setStatusQuestionNotFoundTest() {
        lectureRepository.save(l3);
        userRepository.save(user3);

        assertThrows(QuestionNotFoundException.class, () ->
                questionService.setStatus("editing", q3.getId(), user3.getUid(), l3.getModkey()));

        lectureRepository.deleteById(l3.getUuid());
        userRepository.deleteById(uid3);
    }

    @Test
    void setStatusNoLectureTest() {
        questionRepository.save(q3);

        assertThrows(LectureNotFoundException.class, () ->
                questionService.setStatus("editing", q3.getId(), user3.getUid(), l3.getModkey()));

        QuestionEntity q = questionRepository.findById(q3.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(q3.getStatus(), q.getStatus());
        assertEquals(q3.getEditorId(), q.getEditorId());

        questionRepository.deleteById(q3.getId());
    }

    @Test
    void setStatusWrongModkeyTest() {
        assertThrows(InvalidModkeyException.class, () ->
                questionService.setStatus("editing", q2.getId(), user2.getUid(), l1.getModkey()));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(q2.getStatus(), q.getStatus());
        assertEquals(q2.getEditorId(), q.getEditorId());
    }

    @Test
    void setStatusAlreadyBeingModifiedTest() {
        q2.setEditorId(uid1);
        q2.setStatus("answering");
        questionRepository.save(q2);

        assertThrows(QuestionAlreadyBeingModifiedException.class, () ->
                questionService.setStatus("editing", q2.getId(), user2.getUid(), l2.getModkey()));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(q2.getStatus(), q.getStatus());
        assertEquals(q2.getEditorId(), q.getEditorId());

        q2.setEditorId(0);
        q2.setStatus("new");
    }

    @Test
    void setStatusStartModifyingTest() throws Exception {
        assertEquals(0, questionService.setStatus("editing",
                q2.getId(), user2.getUid(), l2.getModkey()));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("editing", q.getStatus());
        assertEquals(user2.getUid(), q.getEditorId());
    }

    @Test
    void setStatusStopModifyingTest() throws Exception {
        q2.setEditorId(uid2);
        q2.setStatus("answering");
        questionRepository.save(q2);

        assertEquals(0, questionService.setStatus("new",
                q2.getId(), user2.getUid(), l2.getModkey()));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("new", q.getStatus());
        assertEquals(0, q.getEditorId());
    }
}