package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.Question;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Class for Question tests.
 */
public class QuestionTest {

    private static Question question;
    private static Timestamp time;

    /**
     * Create lecture before starting testing.
     */
    @BeforeAll
    public static void createLecture() {
        question = new Question(UUID.randomUUID(), "How do you do, fellow kids?", 42);
        time = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
    }

    @Test
    public void constructorTest() {
        assertNotNull(question);
    }

    @Test
    public void emptyConstructorTest() {
        Question q = new Question();
        assertNotNull(q);
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
    public void getOwnerIdTest() {
        assertEquals(42,question.getOwnerId());
    }

    @Test
    public void gerCurrentQuestionsNotSetTest() {
        Question.setCurrentQuestions(null);
        assertNull(Question.getCurrentQuestions());
    }

    @Test
    public void gerCurrentLectureSetTest() {
        Question.setCurrentQuestions(new ArrayList<>());
        assertNotNull(Question.getCurrentQuestions());
    }

    @Test
    public void equalsTrueTest() {
        Question q1 = new Question();
        Question q2 = new Question();
        assertEquals(q2, q1);
    }

    @Test
    public void equalsFalseDifferentObjectsTest() {
        Question q1 = new Question();
        String q2 = "LobbyBobby";
        assertNotEquals(q2, q1);
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

    //TODO ADD TEST FOR COMPARE TO
}
