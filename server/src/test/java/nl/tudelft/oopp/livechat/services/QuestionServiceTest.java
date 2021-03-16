package nl.tudelft.oopp.livechat.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
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
        l1 = new LectureEntity("Lecture 1", "Ivo van Kreveld");
        l2 = new LectureEntity("Lecture 2", "Stefan Hugtenburg");
        l3 = new LectureEntity("Lecture 3", "Joana Gonçalves");

        user1 = new UserEntity(uid1, "Koen", new Timestamp(
                System.currentTimeMillis() / 1000 * 1000), true,
                "192.168.1.1", l1.getUuid());

        user2 = new UserEntity(uid2, "Otto", new Timestamp(
                System.currentTimeMillis() / 1000 * 1000), false,
                "192.185.7.3", l2.getUuid());

        user3 = new UserEntity(uid3, "Taico", new Timestamp(
                System.currentTimeMillis() / 1000 * 1000), true,
                "122.162.4.8", l3.getUuid());

        StringBuilder sb = new StringBuilder();
        sb.append("a".repeat(2001));
        longText = sb.toString();

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

        questionRepository.deleteById(q3.getId());
    }

    @Test
    @Order(24)
    void editQuestionLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        questionRepository.save(q2);

        assertEquals(-1, questionService.editQuestion(q2.getId(), l2.getModkey(), "foo", uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());

        l2.reOpen();
        lectureRepository.save(l2);
    }

    @Test
    @Order(25)
    void editQuestionWrongModkeyTest() {
        assertEquals(-1, questionService.editQuestion(q2.getId(), l1.getModkey(), "foo", uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());
    }

    @Test
    @Order(26)
    void editQuestionTooLongTextTest() {
        assertEquals(-1, questionService.editQuestion(q2.getId(), l2.getModkey(), longText, uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());
    }

    @Test
    @Order(27)
    void editQuestionNewOwnerNotRegisteredTest() {
        userRepository.deleteById(uid2);
        assertEquals(-1, questionService.editQuestion(q2.getId(), l2.getModkey(), "bar", uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("How to get 10 for testing?", q.getText());

        userRepository.save(user2);
    }

    @Test
    @Order(28)
    void editQuestionSuccessfulTest() {
        assertEquals(0, questionService.editQuestion(q2.getId(), l2.getModkey(), "bar", uid2));

        QuestionEntity q = questionRepository.findById(q2.getId()).orElse(null);
        assertNotNull(q);
        assertEquals("bar", q.getText());
        assertEquals(uid2, q2.getOwnerId());

        q2 = q;
    }

    /*
    @Test
    @Order(9)
    void editQuestionSuccessfulTest() {
        long qid = q1.getId();
        long newOwnerId = 42L;
        UUID modKey = l1.getModkey();
        String newText = "new text)))";

        int result = questionService.editQuestion(qid, modKey, newText, newOwnerId);
        q1 = questionRepository.findById(qid).orElse(null);
        assertEquals(0, result);
        assertEquals(newText, q1.getText());
    }


    @Test
    @Order(10)
    void editQuestionUnsuccessfulTest() {
        long qid = q1.getId();
        long newOwnerId = 42L;
        String oldText = q1.getText();
        UUID modKey = l2.getModkey();
        String newText = "new text)))";

        int result = questionService.editQuestion(qid, modKey, newText, newOwnerId);
        assertEquals(-1, result);

        QuestionEntity q = questionRepository.findById(qid).orElse(null);
        assertNotNull(q);
        assertEquals(oldText, q.getText());
    }

    @Test
    @Order(11)
    void upvoteSuccessfulTest() {
        long qid = q1.getId();
        long uid = 27L;
        final int oldVotes = q1.getVotes();

        final int result = questionService.upvote(qid, uid);
        q1 = questionRepository.findById(qid).orElse(null);
        assertNotNull(q1);
        assertEquals(0, result);
        assertEquals(oldVotes + 1, q1.getVotes());
    }

    @Test
    @Order(12)
    void unvoteSuccessfulTest() {
        long qid = q1.getId();
        long uid = 27L;
        final int oldVotes = q1.getVotes();

        questionService.upvote(qid, uid);
        final int result = questionService.upvote(qid, uid);
        q1 = questionRepository.findById(qid).orElse(null);
        assertNotNull(q1);
        assertEquals(0, result);
        assertEquals(oldVotes, q1.getVotes());
    }

    @Test
    @Order(13)
    void askQuestionWhenLectureIsClosed() {
        LectureEntity l = lectureRepository.findLectureEntityByUuid(q1.getLectureId());
        l.close();
        lectureRepository.save(l);

        long result = questionService.newQuestionEntity(q3);        //q3 belongs to lecture 1
        assertEquals(-1, result);
    }


    @Test
    @Order(15)
    void upvoteUnsuccessfulTest() {
        long qid = -1;
        long uid = 27L;

        final int result = questionService.upvote(qid, uid);
        q1 = questionRepository.findById(qid).orElse(null);
        assertNull(q1);
        assertEquals(-1, result);
    }

    @Test
    @Order(16)
    void answerQuestionSuccessfulTest() {
        long qid = q1.getId();
        UUID modKey = l1.getModkey();

        int result = questionService.answer(qid, modKey, "this is the answer to a question");
        assertEquals(0, result);
        QuestionEntity q1after = questionRepository.findById(qid).orElse(null);
        assertTrue(q1after.isAnswered());
    }

    @Test
    @Order(17)
    void answerQuestionUnsuccessfulTest() {
        long qid = q1.getId();
        UUID modKey = UUID.randomUUID();

        int result = questionService.answer(qid, modKey,
                "this question is so stupid man, what are you thinking");

        assertEquals(-1, result);
        QuestionEntity q1after = questionRepository.findById(qid).orElse(null);
        assertFalse(q1after.isAnswered());
    }

    @Test
    @Order(17)
    void answerQuestionUnsuccessfulLongTest() {
        long qid = q1.getId();
        UUID modKey = l1.getModkey();

        int result = questionService.answer(qid, modKey,
                "CULO".repeat(600));

        assertEquals(-1, result);
        QuestionEntity q1after = questionRepository.findById(qid).orElse(null);
        assertFalse(q1after.isAnswered());
    }

    @Test
    @Order(18)
    void editQuestionUnsuccessful2Test() {
        long qid = 112233;
        long newOwnerId = 42L;
        String oldText = q1.getText();
        UUID modKey = l2.getModkey();
        String newText = "new text)))";

        int result = questionService.editQuestion(qid, modKey, newText, newOwnerId);
        assertEquals(-1, result);

        QuestionEntity q = questionRepository.findById(qid).orElse(null);
        assertNull(q);
    }

    @Test
    @Order(18)
    void editQuestionUnsuccessful3Test() {
        long qid = q1.getId();
        long newOwnerId = 42L;
        String oldText = q1.getText();
        UUID modKey = l2.getModkey();
        String newText = "new text)))";

        lectureService.close(l1.getUuid(), l1.getModkey());
        int result = questionService.editQuestion(qid, modKey, newText, newOwnerId);
        assertEquals(-1, result);

        QuestionEntity q = questionRepository.findById(qid).orElse(null);
        assertNotNull(q);
    }*/

}