package nl.tudelft.oopp.livechat.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class QuestionEntityTest {
    private static final UUID lectureId = UUID.randomUUID();
    private static final Long ownerId = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
    private static QuestionEntity questionEntity;
    private static final Timestamp time = new Timestamp(System.currentTimeMillis() / 1000 * 1000);

    @BeforeAll
    static void setUp() {
        questionEntity = new QuestionEntity(lectureId,
                "What is the answer to the Ultimate "
                        + "Question of Life, the Universe, and Everything?",
                null, ownerId);
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
    void setLectureIdTest() {
        questionEntity.setLectureId(lectureId);
        assertEquals(lectureId, questionEntity.getLectureId());
    }

    @Test
    void getLectureTest() {
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
        assertNull(questionEntity.getAnswerTime());
    }

    @Test
    void setAnswerTimeTest() {
        Timestamp t = time;
        questionEntity.setAnswerTime(t);
        assertEquals(t, questionEntity.getAnswerTime());
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
}