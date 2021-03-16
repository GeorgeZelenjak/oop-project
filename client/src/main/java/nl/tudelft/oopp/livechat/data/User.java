package nl.tudelft.oopp.livechat.data;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * User class.
 */
public class User {
    private static String userName;

    private static long uid;

    private static Set<Long> askedQuestionIds = new HashSet<>();

    /**
     * Sets uid.
     */
    //TODO change this method when we come up with another authentication method
    public static void setUid() {
        uid = ThreadLocalRandom.current().nextLong(1000000L, Long.MAX_VALUE);
    }

    /**
     * Gets uid.
     * @return the uid
     */
    public static long getUid() {
        return uid;
    }

    /**
     * Sets user name.
     * @param name the name
     */
    public static void setUserName(String name) {
        userName = name;
    }

    /**
     * Gets user name.
     * @return the user name
     */
    public static String getUserName() {
        return userName;
    }

    /**
     * Gets the set of asked question ids.
     * @return the set of asked question ids
     */
    public static Set<Long> getAskedQuestionIds() {
        return askedQuestionIds;
    }

    /**
     * Sets the set of asked question ids.
     * @param questionIds the set of asked question ids
     */
    @SuppressWarnings("unused")
    public static void setAskedQuestionsIds(Set<Long> questionIds) {
        askedQuestionIds = questionIds;
    }

    /**
     * Adds a question id to the set of asked question ids.
     * @param qid the question id to be added
     * @return true if added, false otherwise
     */
    public static boolean addQuestionId(Long qid) {
        return askedQuestionIds.add(qid);
    }

    /**
     * Deletes question id from the set of asked question ids.
     * @param questionId the question id to be deleted
     */
    public static void deleteQuestionId(Long questionId) {
        try {
            askedQuestionIds.remove(questionId);
        } catch (Exception e) {
            System.err.println("Id not deleted");
        }
    }
}
