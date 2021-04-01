package nl.tudelft.oopp.livechat.businesslogic;

import nl.tudelft.oopp.livechat.data.Lecture;
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


    /**
     * Setup for the test class.
     */
    @BeforeAll
    public static void setup() {
        Lecture.setCurrent(new Lecture());
        q1 = new Question();
        q1.setId(452345135432L);
        q1.setVotes(4);
        q1.setTime(new Timestamp(System.currentTimeMillis()));
        q1.setAnswered(true);

        q2 = new Question();
        q2.setId(523452345542L);
        q2.setVotes(3);
        q2.setTime(new Timestamp(System.currentTimeMillis() + 5000));

        q3 = new Question();
        q3.setId(1232345345432L);
        q3.setVotes(2);
        q3.setTime(new Timestamp(System.currentTimeMillis() + 15000));

        q4 = new Question();
        q4.setId(95876324526432L);
        q4.setVotes(1);
        q4.setTime(new Timestamp(System.currentTimeMillis() + 25000));
        q4.setAnswered(true);
    }

    @Test
    public void sortByVotesTest() {
        List<Question> questionsToSort = new ArrayList<>();
        q3.setVotes(3);
        questionsToSort.add(q4);
        questionsToSort.add(q2);
        questionsToSort.add(q3);
        questionsToSort.add(q1);

        List<Question> questionsSorted = new ArrayList<>();
        questionsSorted.add(q1);
        questionsSorted.add(q3);
        questionsSorted.add(q2);
        questionsSorted.add(q4);

        QuestionManager.sort(true, false, questionsToSort);
        assertEquals(questionsToSort, questionsSorted);

        q3.setVotes(2);
    }

    @Test
    public void sortByTimeTest() {
        List<Question> questionsToSort = new ArrayList<>();
        questionsToSort.add(q1);
        questionsToSort.add(q3);
        questionsToSort.add(q2);
        questionsToSort.add(q4);

        List<Question> questionsSorted = new ArrayList<>();
        questionsSorted.add(q4);
        questionsSorted.add(q3);
        questionsSorted.add(q2);
        questionsSorted.add(q1);

        QuestionManager.sort(false, true, questionsToSort);
        assertEquals(questionsToSort, questionsSorted);
    }

    @Test
    public void sortByBothTest() {
        List<Question> questionsToSort = new ArrayList<>();
        questionsToSort.add(q2);
        questionsToSort.add(q1);
        questionsToSort.add(q4);
        questionsToSort.add(q3);

        // "smaller" will be the question with highest rank:
        //   the number of seconds since the start of the lecture plus the number
        //   of votes times 42 plus 256 if the question is not answered
        List<Question> questionsSorted = new ArrayList<>();
        questionsSorted.add(q2); //q2:  5 + 42 * 3 + 256 = 387
        questionsSorted.add(q3); //q3: 15 + 42 * 2 + 256 = 355
        questionsSorted.add(q1); //q1:  0 + 42 * 4 +   0 = 168
        questionsSorted.add(q4); //q4: 25 + 42 * 1 +   0 =  67

        QuestionManager.sort(true, true, questionsToSort);
        assertEquals(questionsToSort, questionsSorted);
    }

    @Test
    public void filterNoneTest() {
        List<Question> questionsToFilter = new ArrayList<>();
        questionsToFilter.add(q2);
        questionsToFilter.add(q1);
        questionsToFilter.add(q4);
        questionsToFilter.add(q3);

        List<Question> filtered = new ArrayList<>();
        filtered.add(q2);
        filtered.add(q1);
        filtered.add(q4);
        filtered.add(q3);

        assertEquals(filtered, QuestionManager
                .filter(true,true,questionsToFilter));
    }

    @Test
    public void filterAnsweredTest() {
        List<Question> questionsToFilter = new ArrayList<>();
        questionsToFilter.add(q2);
        questionsToFilter.add(q1);
        questionsToFilter.add(q4);
        questionsToFilter.add(q3);

        List<Question> filtered = new ArrayList<>();
        filtered.add(q1);
        filtered.add(q4);

        assertEquals(filtered, QuestionManager
                .filter(true,false,questionsToFilter));
    }

    @Test
    public void filterUnansweredTest() {
        List<Question> questionsToFilter = new ArrayList<>();
        questionsToFilter.add(q2);
        questionsToFilter.add(q1);
        questionsToFilter.add(q4);
        questionsToFilter.add(q3);

        List<Question> filtered = new ArrayList<>();
        filtered.add(q2);
        filtered.add(q3);

        assertEquals(filtered, QuestionManager
                .filter(false,true,questionsToFilter));
    }

    @Test
    public void filterAllTestTest() {
        List<Question> questionsToFilter = new ArrayList<>();
        questionsToFilter.add(q2);
        questionsToFilter.add(q1);
        questionsToFilter.add(q4);
        questionsToFilter.add(q3);

        assertEquals(new ArrayList<Question>(),  QuestionManager
                .filter(false,false,questionsToFilter));
    }




}
