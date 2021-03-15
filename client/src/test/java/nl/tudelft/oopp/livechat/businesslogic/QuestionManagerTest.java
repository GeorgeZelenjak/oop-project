package nl.tudelft.oopp.livechat.businesslogic;

import nl.tudelft.oopp.livechat.data.Question;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



public class QuestionManagerTest {

    private static Question q1;
    private static Question q2;
    private static Question q3;
    private static Question q4;
    private static List<Question> questionsToFilter;


    @BeforeAll
    private static void setup() {
        q1 = new Question();
        q1.setVotes(1);
        q1.setTime(new Timestamp(4));
        q1.setAnswered(true);

        q2 = new Question();
        q2.setVotes(2);
        q2.setTime(new Timestamp(3));

        q3 = new Question();
        q3.setVotes(3);
        q3.setTime(new Timestamp(2));
        q3.setAnswered(true);

        q4 = new Question();
        q4.setVotes(4);
        q4.setTime(new Timestamp(1));
        q4.setAnswered(true);

        questionsToFilter = new ArrayList<Question>();
        questionsToFilter.add(q2);
        questionsToFilter.add(q1);
        questionsToFilter.add(q4);
        questionsToFilter.add(q3);
    }

    @Test
    public void sortByVotesTest() {
        List<Question> questionsToSort = new ArrayList<Question>();
        questionsToSort.add(q2);
        questionsToSort.add(q1);
        questionsToSort.add(q4);
        questionsToSort.add(q3);

        List<Question> questionsSorted = new ArrayList<Question>();
        questionsSorted.add(q4);
        questionsSorted.add(q3);
        questionsSorted.add(q2);
        questionsSorted.add(q1);

        assertNotEquals(questionsSorted.get(0).getVotes(),questionsToSort.get(0).getVotes());
        QuestionManager.sort(true,questionsToSort);
        assertEquals(questionsSorted.get(0).getVotes(),questionsToSort.get(0).getVotes());

    }

    @Test
    public void sortByTimeTest() {
        List<Question> questionsToSort = new ArrayList<Question>();
        questionsToSort.add(q2);
        questionsToSort.add(q1);
        questionsToSort.add(q4);
        questionsToSort.add(q3);

        List<Question> questionsSorted = new ArrayList<Question>();
        questionsSorted.add(q1);
        questionsSorted.add(q2);
        questionsSorted.add(q3);
        questionsSorted.add(q4);

        assertNotEquals(questionsSorted.get(0).getTime(),questionsToSort.get(0).getTime());
        QuestionManager.sort(false,questionsToSort);
        assertEquals(questionsSorted.get(0).getTime(),questionsToSort.get(0).getTime());

    }

    @Test
    public void filterNoneTest() {
        List<Question> filteredQuestions = QuestionManager
                .filter(true,true,questionsToFilter);
        assertEquals(4,filteredQuestions.size());
    }

    @Test
    public void filterAnsweredTest() {
        List<Question> filteredQuestions = QuestionManager
                .filter(true,false,questionsToFilter);
        assertEquals(3,filteredQuestions.size());
    }

    @Test
    public void filterUnansweredTest() {
        List<Question> filteredQuestions = QuestionManager
                .filter(false,true,questionsToFilter);
        assertEquals(1,filteredQuestions.size());
    }

    @Test
    public void filterAllTestTest() {
        List<Question> filteredQuestions = QuestionManager
                .filter(false,false,questionsToFilter);
        assertEquals(0,filteredQuestions.size());
    }




}
