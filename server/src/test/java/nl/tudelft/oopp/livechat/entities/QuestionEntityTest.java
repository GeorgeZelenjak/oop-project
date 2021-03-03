package nl.tudelft.oopp.livechat.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class QuestionEntityTest {
    private static UUID lectureId = UUID.randomUUID();
    private static Long ownerId = ThreadLocalRandom.current().nextLong(1000000000000L);
    private static LocalDateTime time = LocalDateTime.now();
    private static QuestionEntity questionEntity;

    @BeforeAll
    static void setUp() {
        questionEntity = new QuestionEntity(lectureId,
                "What is the answer to the Ultimate "
                        + "Question of Life, the Universe, and Everything?",
                "42", time, ownerId);
    }

    @Test
    void constructorTest() {
        assertNotNull(questionEntity);
    }

    @Test
    void getIdTest() {
        //cannot test id, because I have no access to it and it is randomly generated
        assertNotNull(questionEntity.getId());
    }


    @Test
    void getLectureTest() {
        assertEquals(lectureId, questionEntity.getLecture());
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
    void getTextTest() {
        String text = new String("What is the answer to the Ultimate Question of Life,"
                + " the Universe, and Everything?");
        assertEquals(text, questionEntity.getText());
    }

    @Test
    void setTextTest() {
        String newText = new String("What is the answer to the ultimate question of Life, "
                + "the Universe, and Everything?");
        questionEntity.setText(newText);
        assertEquals(new String(newText), questionEntity.getText());
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
        assertEquals(new String("42"), questionEntity.getAnswerText());
    }

    @Test
    void setAnswerTextTest() {
        questionEntity.setAnswerText("forty-two");
        assertEquals(new String("forty-two"), questionEntity.getAnswerText());
    }

    @Test
    void getAnswerTimeTest() {
        assertNull(questionEntity.getAnswerTime());
    }

    @Test
    void setAnswerTimeTest() {
        LocalDateTime t = time.now();
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
    void testEqualsNullTest() {
        assertNotEquals(questionEntity, null);
    }

    @Test
    void testEqualsSameTest() {
        assertEquals(questionEntity, questionEntity);
    }

    /*@Test
    void testEqualsDifferent() {
        QuestionEntity q = new QuestionEntity(lectureId, "What is the answer to the "
        + "Ultimate Question of Life,the Universe, and Everything?",
                "42", time, ownerId);
        assertNotEquals(questionEntity, q);
    }*/

    @Test
    void testHashCodeTest() {
        int hash = Objects.hash(questionEntity.getId(), lectureId, time);
        assertEquals(hash, questionEntity.hashCode());
    }
}