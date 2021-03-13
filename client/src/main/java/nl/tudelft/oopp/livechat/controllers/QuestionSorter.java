package nl.tudelft.oopp.livechat.controllers;


import nl.tudelft.oopp.livechat.data.Question;

import java.util.Collections;
import java.util.List;

public class QuestionSorter {
    public static void sort(boolean byVotes, List<Question> questions) {
        if (byVotes) {
            Collections.sort(questions, (q1, q2) -> {
                int result = Integer.compare(q2.getVotes(), q1.getVotes());
                if (result != 0) return result;
                return q2.getTime().compareTo(q1.getTime());
            });
        } else {
            Collections.sort(questions);
        }
    }
}
