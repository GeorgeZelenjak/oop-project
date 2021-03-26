package nl.tudelft.oopp.livechat.entities;

import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Objects;

@Table(name = "userquestion")
@Entity
@IdClass(UserQuestionPairId.class)
@EnableTransactionManagement
public class UserQuestionTable {

    @Id
    long userId;

    @Id
    long questionId;

    /**
     * Creates a new UserQuestionTable object.
     */
    public UserQuestionTable() {
    }

    /**
     * Creates a new UserQuestionTable object with the specified parameters.
     * @param uid the id of the user
     * @param qid the id of the question
     */
    public UserQuestionTable(long uid, long qid) {
        this.userId = uid;
        this.questionId = qid;
    }

    /**
     * Gets the id of the user.
     * @return the id of the user
     */
    public long getUserId() {
        return userId;
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
        return questionId;
    }

    /**
     * Sets the id of the question.
     * @param questionId the id of the question
     */
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    /**
     * Compares the UserQuestionTable to another object.
     * @param other the object to compare to
     * @return true iff another object is also UserQuestionTable
     *         and has the same user id and question id.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof UserQuestionTable) {
            UserQuestionTable that = (UserQuestionTable) other;
            return this.userId == that.userId && this.questionId == that.questionId;
        }
        return false;
    }

    /**
     * Creates the hash code for the UserQuestionTable object.
     * @return the created hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.questionId);
    }
}
