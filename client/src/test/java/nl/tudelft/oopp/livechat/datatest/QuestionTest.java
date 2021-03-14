package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Class for Question tests.
 */
public class QuestionTest {

    public static Question question;

    /**
     * Create lecture before starting testing.
     */
    @BeforeAll
    public static void createLecture() {
        question = new Question(UUID.randomUUID(), "How do you do, fellow kids?", 42);
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
    public void emptyConstructorTest() {
        Question question1 = new Question();

        assertEquals(0, question1.getId());
        assertEquals(0, question1.getVotes());
        assertEquals(0, question1.getOwnerId());

        assertNull(question1.getText());
        assertNull(question1.getLectureId());
        assertNull(question1.getTime());
        assertNull(question1.getAnswerText());
        assertNull(question1.getAnswerText());

        assertFalse(question1.isAnswered());
    }

    @Test
    public void gerCurrentQuestionsNotSetTest() {
        assertNull(Question.getCurrentQuestions());
    }

    @Test
    public void gerCurrentLectureSetTest() {
        Question.setCurrentQuestions(new ArrayList<Question>());
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
