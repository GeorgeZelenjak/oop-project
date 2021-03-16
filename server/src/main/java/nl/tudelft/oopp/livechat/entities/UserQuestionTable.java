package nl.tudelft.oopp.livechat.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Table(name = "userquestion")
@Entity
@IdClass(UserQuestionPairId.class)
public class UserQuestionTable {

    @Id
    long userId;

    @Id
    long questionId;

    public UserQuestionTable() {
    }

    public UserQuestionTable(long uid, long qid) {
        this.userId = uid;
        this.questionId = qid;
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
}
