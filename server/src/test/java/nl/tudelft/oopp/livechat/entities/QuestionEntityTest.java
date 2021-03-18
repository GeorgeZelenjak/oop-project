package nl.tudelft.oopp.livechat.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.*;

/**
 * Class for Question entity tests.
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class QuestionEntityTest {
    private static final UUID lectureId = UUID.randomUUID();
    private static final Long ownerId = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
    private static QuestionEntity questionEntity;
    private static final Timestamp time = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
    private static Timestamp answerTime;

    /**
     * Sets up a question before starting testing.
     */
    @BeforeAll
    static void setUp() throws InterruptedException {
        questionEntity = new QuestionEntity(lectureId,
                "What is the answer to the Ultimate "
                        + "Question of Life, the Universe, and Everything?",
                time, ownerId);
        questionEntity.setId(14512412354553456L);
        Thread.sleep(2000);
        answerTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        questionEntity.setAnswerTime(answerTime);
        questionEntity.setOwnerName("predator");
    }

    @Test
    void constructorTest() {
        assertNotNull(questionEntity);
    }

    @Test
    void staticConstructorTest() {
        QuestionEntity question = QuestionEntity.create(lectureId,
                "What is the answer to the Ultimate "
                        + "Question of Life, the Universe, and Everything?",
                new Timestamp(System.currentTimeMillis()), ownerId);
        assertNotNull(question);
    }

    @Test
    void getIdTest() {
        assertEquals(14512412354553456L, questionEntity.getId());
    }

    @Test
    void setIdTest() {
        questionEntity.setId(53451341241431L);
        assertEquals(53451341241431L, questionEntity.getId());
    }

    @Test
    void getLectureIdTest() {
        assertEquals(lectureId, questionEntity.getLectureId());
    }

    @Test
    void setLectureIdTest() {
        questionEntity.setLectureId(lectureId);
        assertEquals(lectureId, questionEntity.getLectureId());
    }

    @Test
    void getTimeTest() {
        assertEquals(time, questionEntity.getTime());
    }

    @Test
    void getVotesTest() {
        assertEquals(0, questionEntity.getVotes());
    }

    @Test
    void voteTest() {
        int oldVotes = questionEntity.getVotes();
        questionEntity.vote();
        assertEquals(oldVotes + 1, questionEntity.getVotes());
    }

    @Test
    void unvoteTest() {
        int oldVotes = questionEntity.getVotes();
        questionEntity.unvote();
        assertEquals(oldVotes - 1, questionEntity.getVotes());
    }

    @Test
    void getTextTest() {
        String text = "What is the answer to the Ultimate Question of Life,"
                + " the Universe, and Everything?";
        assertEquals(text, questionEntity.getText());
    }

    @Test
    void setTextTest() {
        String newText = "What is the answer to the ultimate question of Life, "
                + "the Universe, and Everything?";
        questionEntity.setText(newText);
        assertEquals(newText, questionEntity.getText());
    }

    @Test
    void isAnsweredTest() {
        assertFalse(questionEntity.isAnswered());
    }

    @Test
    void setAnsweredTest() {
        questionEntity.setAnswered(true);
        assertTrue(questionEntity.isAnswered());
    }

    @Test
    void getAnswerTextTest() {
        questionEntity.setAnswerText("42");
        assertEquals("42", questionEntity.getAnswerText());
    }

    @Test
    void setAnswerTextTest() {
        questionEntity.setAnswerText("forty-two");
        assertEquals("forty-two", questionEntity.getAnswerText());
    }

    @Test
    void getAnswerTimeTest() {
        assertEquals(answerTime, questionEntity.getAnswerTime());
    }

    @Test
    void setAnswerTimeTest() throws InterruptedException {
        Thread.sleep(2000);
        Timestamp newAnswerTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        questionEntity.setAnswerTime(newAnswerTime);
        assertEquals(newAnswerTime, questionEntity.getAnswerTime());
    }

    @Test
    void getOwnerNameTest() {
        assertEquals("predator", questionEntity.getOwnerName());
    }

    @Test
    void setOwnerNameTest() {
        questionEntity.setOwnerName("alien");
        assertEquals("alien", questionEntity.getOwnerName());
    }

    @Test
    void getOwnerIdTest() {
        assertEquals(ownerId, questionEntity.getOwnerId());
    }

    @Test
    void setOwnerIdTest() {
        long newOwnerId = ThreadLocalRandom.current().nextLong(1000000000000L);
        questionEntity.setOwnerId(newOwnerId);
        assertEquals(newOwnerId, questionEntity.getOwnerId());
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(questionEntity, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(questionEntity, questionEntity);
    }

    @Test
    void equalsEqualTest() {
        QuestionEntity q = new QuestionEntity(lectureId,
                "I am the question with different text", time, ownerId);
        q.setId(questionEntity.getId());
        assertEquals(questionEntity, q);
    }

    @Test
    void equalsDifferentTest() {
        QuestionEntity q = new QuestionEntity(lectureId, "What is the answer to the "
                + "Ultimate Question of Life,the Universe, and Everything?", time, ownerId);
        assertNotEquals(questionEntity, q);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(questionEntity.getId(), lectureId, time);
        assertEquals(hash, questionEntity.hashCode());
    }

    @Test
    void toStringTest() {
        String expected = "QuestionEntity{"
                + "id=" + questionEntity.getId()
                + ", lectureId=" + lectureId
                + ", time=" + time
                + ", votes=" + questionEntity.getVotes()
                + ", text='" + questionEntity.getText() + '\''
                + ", answered=" + questionEntity.isAnswered()
                + ", answerText='" + questionEntity.getAnswerText() + '\''
                + ", answerTime=" + questionEntity.getAnswerTime()
                + ", ownerId=" + questionEntity.getOwnerId()
                + '}';

        assertEquals(expected, questionEntity.toString());
    }

    @Test
    void nullTimeTest() {
        QuestionEntity q = new QuestionEntity(UUID.randomUUID(), "lalala", null, 12L);
        assertNotNull(q.getTime());
    }
}