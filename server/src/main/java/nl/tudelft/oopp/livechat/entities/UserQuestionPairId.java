package nl.tudelft.oopp.livechat.entities;

import java.io.Serializable;
import java.util.Objects;

public class UserQuestionPairId implements Serializable {


    long userId;

    long questionId;

    public UserQuestionPairId() {
    }

    public UserQuestionPairId(long userId, long questionId) {
        this.userId = userId;
        this.questionId = questionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserQuestionPairId that = (UserQuestionPairId) o;
        return userId == that.userId && questionId == that.questionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, questionId);
    }
}
