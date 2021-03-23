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
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserQuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * Class for Question service tests.
 * Warning: These tests depend on the order annotation because sometimes the outcome of one test
 *  is dependent on the outcome of the test before it (like for example, adding a user or a lecture
 *  to the repository, etc).
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
        l2 = new LectureEntity("Lecture 2", "Stefan Hugtenburg", time);
        l3 = new LectureEntity("Lecture 3", "Joana Gonçalves", time);

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
        q2 = new QuestionEntity(l2.getUuid(), longText,
                new Timestamp(System.currentTimeMillis()), uid2);
        q3 = new QuestionEntity(l3.getUuid(), "how old?",
                new Timestamp(System.currentTimeMillis()), uid3);

    }

    @Test
    @Order(1)
    void constructorTest() {
        assertNotNull(questionService);
    }

    /**
     * Tests related to newQuestionEntity method.
     */
    @Test
    @Order(2)
    void newQuestionEntityTestAndNonStaticSetup() {
        lectureRepository.save(l1);
        userRepository.save(user1);

        long result = questionService.newQuestionEntity(q1);
        assertTrue(result > 0);
        assertTrue(questionRepository.findById(result).isPresent());
    }

    @Test
    @Order(3)
    void newQuestionEntityQuestionIsAskedTest() {
        long result = questionService.newQuestionEntity(q1);
        assertEquals(-1, result);
    }

    @Test
    @Order(4)
    void newQuestionEntityLectureDoesNotExistTest() {
        long result = questionService.newQuestionEntity(q2);
        assertEquals(-1, result);
    }

    @Test
    @Order(5)
    void newQuestionEntityLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        long result = questionService.newQuestionEntity(q2);
        assertEquals(-1, result);

        l2.reOpen();
        lectureRepository.save(l2);
    }

    @Test
    @Order(6)
    void newQuestionEntityTooLongTextTest() {
        userRepository.save(user2);
        long result = questionService.newQuestionEntity(q2);
        assertEquals(-1, result);

        q2.setText("How to get 10 for testing?");
    }

    @Test
    @Order(7)
    void newQuestionEntityOwnerNotRegisteredTest() {
        userRepository.deleteById(uid2);

        long result = questionService.newQuestionEntity(q2);
        assertEquals(-1, result);

        userRepository.save(user2);
    }

    /**
     * Tests related to getQuestionsByLectureId method.
     */
    @Test
    @Order(8)
    void getQuestionsByLectureIdSuccessfulTest() {
        List<QuestionEntity> qs = questionService
                .getQuestionsByLectureId(UUID.fromString(l1.getUuid().toString()));
        assertEquals(1, qs.size());
        assertEquals(q1, qs.get(0));
    }

    @Test
    @Order(9)
    void getQuestionsByLectureIdNoQuestionsTest() {
        List<QuestionEntity> qs = questionService
                .getQuestionsByLectureId(UUID.fromString(l2.getUuid().toString()));
        assertEquals(0, qs.size());
    }

    @Test
    @Order(10)
    void getQuestionsByLectureIdNoLectureTest() {
        List<QuestionEntity> qs = questionService
                .getQuestionsByLectureId(UUID.fromString(l3.getUuid().toString()));
        assertEquals(0, qs.size());
    }

    /**
     * Tests related to deleteQuestion method.
     */
    @Test
    @Order(11)
    void deleteQuestionNoQuestionTest() {
        assertEquals(-1, questionService.deleteQuestion(q3.getId(), uid3));
    }

    @Test
    @Order(12)
    void deleteQuestionWrongUidTest() {
        assertEquals(-1, questionService.deleteQuestion(q1.getId(), uid3));
        assertTrue(questionRepository.findById(q1.getId()).isPresent());
    }

    @Test
    @Order(13)
    void deleteQuestionNoLectureTest() {
        questionRepository.save(q3);
        userRepository.save(user3);
        assertEquals(-1, questionService.deleteQuestion(q3.getId(), uid3));
        assertTrue(questionRepository.findById(q3.getId()).isPresent());

        questionRepository.deleteById(q3.getId());
        userRepository.deleteById(uid3);
    }

    @Test
    @Order(14)
    void deleteQuestionLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        questionRepository.save(q2);

        assertEquals(-1, questionService.deleteQuestion(q2.getId(), uid2));
        assertTrue(questionRepository.findById(q2.getId()).isPresent());

        l2.reOpen();
        lectureRepository.save(l2);
    }

    @Test
    @Order(15)
    void deleteQuestionUserNotRegisteredTest() {
        lectureRepository.save(l3);
        questionRepository.save(q3);

        assertEquals(-1, questionService.deleteQuestion(q3.getId(), uid3));
        assertTrue(questionRepository.findById(q3.getId()).isPresent());

        lectureRepository.deleteById(l3.getUuid());
        questionRepository.deleteById(q3.getId());
    }

    @Test
    @Order(16)
    void deleteQuestionSuccessfulTest() {
        assertEquals(0, questionService.deleteQuestion(q2.getId(), uid2));
        assertTrue(questionRepository.findById(q2.getId()).isEmpty());
        assertTrue(userQuestionRepository.getAllByQuestionId(q2.getId()).isEmpty());
    }

    /**
     * Tests related to deleteModeratorQuestion method.
     */

    @Test
    @Order(17)
    void deleteModeratorQuestionNoQuestionTest() {
        assertEquals(-1, questionService.deleteModeratorQuestion(q3.getId(), l3.getModkey()));
    }

    @Test
    @Order(18)
    void deleteModeratorQuestionNoLectureTest() {
        questionRepository.save(q3);
        assertEquals(-1, questionService.deleteModeratorQuestion(q3.getId(), l3.getModkey()));
        assertTrue(questionRepository.findById(q3.getId()).isPresent());

        questionRepository.deleteById(q3.getId());
    }

    @Test
    @Order(19)
    void deleteModeratorQuestionLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        questionRepository.save(q2);

        assertEquals(-1, questionService.deleteModeratorQuestion(q2.getId(), l2.getModkey()));
        assertTrue(questionRepository.findById(q2.getId()).isPresent());

        l2.reOpen();
        lectureRepository.save(l2);
    }

    @Test
    @Order(20)
    void deleteModeratorQuestionWrongModkeyTest() {
        assertEquals(-1, questionService.deleteModeratorQuestion(q2.getId(), l1.getModkey()));
        assertTrue(questionRepository.findById(q2.getId()).isPresent());
    }

    @Test
    @Order(21)
    void deleteModeratorQuestionSuccessfulTest() {
        assertEquals(0, questionService.deleteModeratorQuestion(q2.getId(), l2.getModkey()));
        assertTrue(questionRepository.findById(q2.getId()).isEmpty());
        assertTrue(userQuestionRepository.getAllByQuestionId(q2.getId()).isEmpty());
    }

    /**
     * Tests related to editQuestion method.
     */
    @Test
    @Order(22)
    void editQuestionNoQuestionTest() {
        assertEquals(-1, questionService.editQuestion(q3.getId(), l3.getModkey(), "bla bla", uid1));
    }

    @Test
    @Order(23)
    void editQuestionNoLectureTest() {
        questionRepository.save(q3);
        assertEquals(-1, questionService.editQuestion(q3.getId(), l3.getModkey(), "foo", uid1));

        QuestionEntity q = questionRepository.findById(q3.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("how old?", q.getText());
        assertFalse(q.isEdited());

        questionRepository.deleteById(q3.getId());
    }

    @Test
    @Order(24)
    void editQuestionWrongModkeyTest() {
        questionRepository.save(q2);
        assertEquals(-1, questionService.editQuestion(q2.getId(), l1.getModkey(), "foo", uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());
        assertFalse(q.isEdited());
    }

    @Test
    @Order(25)
    void editQuestionTooLongTextTest() {
        assertEquals(-1, questionService.editQuestion(q2.getId(), l2.getModkey(), longText, uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());
        assertFalse(q.isEdited());
    }

    @Test
    @Order(28)
    void editQuestionNewOwnerNotRegisteredTest() {
        userRepository.deleteById(uid2);
        assertEquals(-1, questionService.editQuestion(q2.getId(), l2.getModkey(), "bar", uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());
        assertFalse(q.isEdited());

        userRepository.save(user2);
    }

    @Test
    @Order(29)
    void editQuestionSuccessfulTest() {
        assertEquals(0, questionService.editQuestion(q2.getId(), l2.getModkey(), "bar", uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("bar", q.getText());
        assertEquals(uid2, q2.getOwnerId());
        assertTrue(q.isEdited());

        q2 = q;
    }

    /**
     * Tests related to upvote method.
     */
    @Test
    @Order(30)
    void upvoteNoQuestionTest() {
        userRepository.save(user3);

        assertEquals(-1, questionService.upvote(q3.getId(), uid3));
    }

    @Test
    @Order(31)
    void upvoteNoLectureTest() {
        questionRepository.save(q3);
        assertEquals(-1, questionService.upvote(q3.getId(), uid3));

        QuestionEntity q = questionRepository.findById(q3.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(0, q.getVotes());

        questionRepository.deleteById(q3.getId());
    }

    @Test
    @Order(32)
    void upvoteLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        questionRepository.save(q2);

        assertEquals(-1, questionService.upvote(q2.getId(), uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(0, q.getVotes());

        l2.reOpen();
        lectureRepository.save(l2);
    }

    @Test
    @Order(33)
    void upvoteNotRegisteredTest() {
        userRepository.deleteById(uid2);
        assertEquals(-1, questionService.upvote(q2.getId(), uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(0, q.getVotes());

        userRepository.save(user2);
    }

    @Test
    @Order(34)
    void upvoteTest() {
        assertEquals(0, questionService.upvote(q2.getId(), uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(1, q.getVotes());

        List<UserQuestionTable> table = userQuestionRepository.getAllByQuestionId(q2.getId());
        UserQuestionTable match = new UserQuestionTable(uid2, q2.getId());
        assertTrue(table.contains(match));

        q2 = q;
    }

    @Test
    @Order(35)
    void unvoteTest() {
        assertEquals(0, questionService.upvote(q2.getId(), uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals(0, q.getVotes());

        List<UserQuestionTable> table = userQuestionRepository.getAllByQuestionId(q2.getId());
        UserQuestionTable match = new UserQuestionTable(uid2, q2.getId());
        assertFalse(table.contains(match));

        q2 = q;
    }

    /**
     * Tests related to answer method.
     */
    @Test
    @Order(36)
    void answerNoQuestionTest() {
        lectureRepository.save(l3);

        assertEquals(-1, questionService.answer(q3.getId(), l3.getModkey(), "42"));

        lectureRepository.deleteById(l3.getUuid());
    }

    @Test
    @Order(37)
    void answerNoLectureTest() {
        questionRepository.save(q3);
        assertEquals(-1, questionService.answer(q3.getId(), l3.getModkey(), "42"));

        QuestionEntity q = questionRepository.findById(q3.getId()).orElse(null);
        assertNotNull(q);
        assertNull(q.getAnswerText());
        assertNull(q.getAnswerTime());
        assertFalse(q.isAnswered());

        questionRepository.deleteById(q3.getId());
    }

    @Test
    @Order(38)
    void answerTooLongTextTest() {
        assertEquals(-1, questionService.answer(q2.getId(), l2.getModkey(), longText));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertNull(q.getAnswerText());
        assertNull(q.getAnswerTime());
        assertFalse(q.isAnswered());
    }

    @Test
    @Order(39)
    void answerWrongModKeyTest() {
        assertEquals(-1, questionService.answer(q2.getId(), l1.getModkey(), "42"));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertNull(q.getAnswerText());
        assertNull(q.getAnswerTime());
        assertFalse(q.isAnswered());
    }

    @Test
    @Order(40)
    void answerSuccessfulTest() {
        assertEquals(0, questionService.answer(q2.getId(), l2.getModkey(), "42"));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("42", q.getAnswerText());
        assertNotNull(q.getAnswerTime());
        assertTrue(q.isAnswered());
        q2 = q;
    }

    @Test
    @Order(41)
    void newQuestionLectureNotStartedTest() {
        l2.setStartTime(new Timestamp(System.currentTimeMillis() + 0xFFFFFFFFFL));
        lectureRepository.save(l2);
        QuestionEntity q3 = new QuestionEntity(l2.getUuid(), "i'm in the past",
                new Timestamp(0), 42L);
        long result = questionService.newQuestionEntity(q3);
        assertEquals(-1, result);
    }
}