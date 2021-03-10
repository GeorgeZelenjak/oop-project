package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.QuestionEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class QuestionEntityTest {

    public static QuestionEntity question;

    @BeforeAll
    public static void createLecture() {
        question = new QuestionEntity(UUID.randomUUID(), "How do you do, fellow kids?", 42);
    }

    @Test
    public void getIdTest() {
        assertEquals(0, question.getId());
    }

    @Test
    public void getLectureIdTest() {
        assertNotNull(question.getLectureId());
    }

    @Test
    public void getTimeTest() {
        assertNull(question.getTime());
    }

    @Test
    public void getTextTest() {
        assertEquals(0, question.getVotes());
    }

    @Test
    public void getVotesTest() {
        assertEquals("How do you do, fellow kids?", question.getText());
    }

    @Test
    public void isAnsweredTest() {
        assertFalse(question.isAnswered());
    }

    @Test
    public void getAnswerTextTest() {
        assertNull(question.getAnswerText());
    }

    @Test
    public void getAnswerTimeTest() {
        assertNull(question.getAnswerTime());
    }

    @Test
    public void getOwnerIdTEst() {
        assertEquals(42,question.getOwnerId());
    }

    @Test
    public void toStringTest() {
        boolean containsNeededInfo = true;
        String res = question.toString();
        if (!res.contains("How do you do, fellow kids?")) {
            containsNeededInfo = false;
        }
        if (!res.contains("" + 42)) {
            containsNeededInfo = false;
        }
        assertTrue(containsNeededInfo);
    }
}
