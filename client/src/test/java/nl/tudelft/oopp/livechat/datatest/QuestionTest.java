package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.Question;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Class for Question tests.
 */
public class QuestionTest {

    private static Question question;
    private static Timestamp time;
    private static Timestamp answerTime;
    private static UUID lectureId;
    private static List<Question> questionList;

    /**
     * Create lecture before starting testing.
     */
    @BeforeAll
    public static void createLecture() throws InterruptedException {
        questionList = List.of(new Question(lectureId, "?", 11),
                new Question(lectureId, "??", 12),
                new Question(lectureId, "???", 13));
        Question.setCurrentQuestions(questionList);

        lectureId = UUID.randomUUID();
        question = new Question(lectureId, "How do you do, fellow kids?", 69);
        time = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        Thread.sleep(1000);
        answerTime = new Timestamp((System.currentTimeMillis()) / 1000 * 1000);

        question.setTime(time);
        question.setVotes(42);
        question.setAnswerText("42");
        question.setAnswerTime(answerTime);
        question.setOwnerName("Eminem");

        Question.setCurrentQuestion(question);
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
        assertEquals(lectureId, question.getLectureId());
    }

    @Test
    public void getTimeTest() {
        assertEquals(time, question.getTime());
    }

    @Test
    public void setTimeTest() {
        Timestamp newTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        question.setTime(newTime);
        assertEquals(newTime, question.getTime());

        //set it back for other tests
        question.setTime(time);
    }

    @Test
    public void getVotesTest() {
        assertEquals(42, question.getVotes());
    }

    @Test
    public void setVotesTest() {
        question.setVotes(69);
        assertEquals(69, question.getVotes());

        //set it back for other tests
        question.setVotes(42);
    }

    @Test
    public void getTextTest() {
        assertEquals("How do you do, fellow kids?", question.getText());
    }

    @Test
    public void setTextTest() {
        question.setText("Hi, my name is... Slim Shady!");
        assertEquals("Hi, my name is... Slim Shady!", question.getText());

        //set it back for other tests
        question.setText("How do you do, fellow kids?");
    }

    @Test
    public void isAnsweredTest() {
        question.setAnswered(true);
        assertTrue(question.isAnswered());
    }

    @Test
    public void setAnsweredTest() {
        boolean answered = question.isAnswered();
        question.setAnswered(!answered);
        assertNotEquals(answered, question.isAnswered());
    }

    @Test
    public void isEditedTest() {
        question.setEdited(true);
        assertTrue(question.isEdited());
    }

    @Test
    public void setEditedTest() {
        boolean edited = question.isEdited();
        question.setEdited(!edited);
        assertNotEquals(edited, question.isEdited());
    }

    @Test
    public void getAnswerTextTest() {
        assertEquals("42", question.getAnswerText());
    }

    @Test
    public void setAnswerTextTest() {
        question.setAnswerText("I'm the real Shady!");
        assertEquals("I'm the real Shady!", question.getAnswerText());

        //set it back for other tests
        question.setAnswerText("42");
    }

    @Test
    public void getAnswerTimeTest() {
        assertEquals(answerTime, question.getAnswerTime());
    }

    @Test
    public void setAnswerTimeTest() {
        Timestamp newAnswerTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        question.setAnswerTime(newAnswerTime);
        assertEquals(newAnswerTime, question.getAnswerTime());

        //set it back for other tests
        question.setAnswerTime(answerTime);
    }

    @Test
    public void getOwnerIdTest() {
        assertEquals(69, question.getOwnerId());
    }

    @Test
    public void setOwnerIdTest() {
        question.setOwnerId(666);
        assertEquals(666, question.getOwnerId());

        //set it back for other tests
        question.setOwnerId(69);
    }

    @Test
    public void getStatusTest() {
        assertEquals("new", question.getStatus());
    }

    @Test
    public void setStatusTest() {
        question.setStatus("answering");
        assertEquals("answering", question.getStatus());

        //set it back for other tests
        question.setStatus("new");
    }

    @Test
    public void getOwnerNameTest() {
        assertEquals("Eminem", question.getOwnerName());
    }

    @Test
    public void setOwnerNameTest() {
        question.setOwnerName("Slim Shady");
        assertEquals("Slim Shady", question.getOwnerName());

        //set it back for other tests
        question.setOwnerName("Eminem");
    }

    @Test
    public void equalsNullTest() {
        assertNotEquals(question, null);
    }

    @Test
    public void equalsSameTest() {
        assertEquals(question, question);
    }

    @Test
    public void equalsEqualTest() {
        Question q1 = new Question(lectureId, "How do you do, fellow kids?", 69);
        assertEquals(question, q1);     //equal, since both ids are 0 now
    }

    @Test
    public void hashCodeTest() {
        int hashCode = Objects.hash(0, lectureId, time);
        assertEquals(hashCode, question.hashCode());
    }

    @Test
    public void toStringTest() {
        boolean containsNeededInfo = true;
        String res = question.toString();
        if (!res.contains("How do you do, fellow kids?")) {
            containsNeededInfo = false;
        }
        if (!res.contains("42")) {
            containsNeededInfo = false;
        }
        assertTrue(containsNeededInfo);
    }

    @Test
    public void compareToLaterTest() throws InterruptedException {
        Question q = new Question(lectureId, "How do you do, fellow kids?", 69);
        Thread.sleep(1000);
        q.setTime(new Timestamp((System.currentTimeMillis()) / 1000 * 1000));

        Thread.sleep(2000);
        question.setTime(new Timestamp((System.currentTimeMillis()) / 1000 * 1000));

        assertEquals(-1, question.compareTo(q));

        //set it back for other tests
        question.setTime(time);
    }

    @Test
    public void compareToSameTimeTest() {
        Question q = new Question(lectureId, "How do you do, fellow kids?", 69);
        q.setTime(question.getTime());

        assertEquals(0, question.compareTo(q));
    }

    @Test
    public void compareToEarlierTest() throws InterruptedException {
        Thread.sleep(1000);
        question.setTime(new Timestamp((System.currentTimeMillis()) / 1000 * 1000));

        Question q = new Question(lectureId, "How do you do, fellow kids?", 69);
        Thread.sleep(2000);
        q.setTime(new Timestamp((System.currentTimeMillis()) / 1000 * 1000));

        assertEquals(1, question.compareTo(q));

        //set it back for other tests
        question.setTime(time);
    }


    @Test
    public void getCurrentQuestionsTest() {
        assertEquals(questionList, Question.getCurrentQuestions());
    }

    @Test
    public void getCurrentLectureSetTest() {
        List<Question> newQuestionList = List.of(new Question(lectureId, "$$", 11),
                new Question(lectureId, "$$$", 13));
        Question.setCurrentQuestions(newQuestionList);
        assertEquals(newQuestionList, Question.getCurrentQuestions());

        //set it back for other tests
        Question.setCurrentQuestions(questionList);
    }

    @Test
    public void getCurrentQuestionTest() {
        assertEquals(question, Question.getCurrentQuestion());
    }

    @Test
    public void setCurrentQuestionTest() {
        Question q = new Question(lectureId, "Hey Yo", 13);
        Question.setCurrentQuestion(q);
        assertEquals(q, Question.getCurrentQuestion());

        //set it back for other tests
        Question.setCurrentQuestion(question);
    }
}
