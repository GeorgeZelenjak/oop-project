package nl.tudelft.oopp.livechat.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class to store a combination of user id and question id
 *       to track which questions the user has asked.
 */
public class UserQuestionPairId implements Serializable {

    private long userId;

    private long questionId;

    /**
     * Creates a new UserQuestionPairId object.
     */
    public UserQuestionPairId() {
    }

    /**
     * Creates a new UserQuestionPairId object with the specified parameters.
     * @param userId the id of the user
     * @param questionId the id of the question
     */
    public UserQuestionPairId(long userId, long questionId) {
        this.userId = userId;
        this.questionId = questionId;
    }

    /**
     * Gets the id of the user.
     * @return the id of the user
     */
    public long getUserId() {
        return this.userId;
    }

    /**
     * Sets the id of the user.
     * @param userId the id of the user
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Gets the id of the question.
     * @return the id of the question
     */
    public long getQuestionId() {
        return this.questionId;
    }

    /**
     * Sets the id of the question.
     * @param questionId the id of the question
     */
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    /**
     * Compares the UserQuestionPairId to another object.
     * @param o the object to compare to
     * @return true iff another object is also UserQuestionPairId
     *          and has the same user id and question id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UserQuestionPairId) {
            UserQuestionPairId that = (UserQuestionPairId) o;
            return this.userId == that.userId && this.questionId == that.questionId;
        }
        return false;
    }

    /**
     * Creates the hash code for the object.
     * @return the created hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.questionId);
    }
}
