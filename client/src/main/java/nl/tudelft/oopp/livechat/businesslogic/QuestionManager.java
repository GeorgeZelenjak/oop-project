package nl.tudelft.oopp.livechat.businesslogic;

import nl.tudelft.oopp.livechat.data.Question;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionManager {
    /**
     * Sorts questions by time or number of votes according to the given arguments.
     * @param byVotes true iff needs to sort by votes
     * @param questions the list of questions to be sorted
     */
    public static void sort(boolean byVotes, List<Question> questions) {
        if (byVotes) {
            questions.sort((q1, q2) -> {
                int result = Integer.compare(q2.getVotes(), q1.getVotes());
                if (result != 0) return result;
                return q2.getTime().compareTo(q1.getTime());
            });
        } else {
            Collections.sort(questions);
        }
    }


    /**
     * Retains only the answered/unanswered questions according to the given arguments.
     * @param answered if true, retain answered questions
     * @param unanswered if true, retain unanswered questions
     * @param questions the list of questions to be filtered
     * @return the filtered list of questions
     */
    public static List<Question> filter(boolean answered, boolean unanswered,
                                        List<Question> questions) {
        //we don't need the answered questions, so we only retain the unanswered
        if (!answered) {
            questions =  questions.stream().filter((q)
                -> !q.isAnswered()).collect(Collectors.toList());
        }
        //we don't need the unanswered questions, so we only retain the answered ones
        //      (which will return the empty list if we don't need both)
        if (!unanswered) {
            return questions.stream().filter(Question::isAnswered).collect(Collectors.toList());
        }
        return questions;
    }
}
