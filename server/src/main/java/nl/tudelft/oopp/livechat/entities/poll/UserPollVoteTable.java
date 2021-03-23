package nl.tudelft.oopp.livechat.entities.poll;

import nl.tudelft.oopp.livechat.entities.UserQuestionPairId;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "userPollVote")
@Entity
@IdClass(UserPollVotePairId.class)
public class UserPollVoteTable {

    @Id
    @Column(name = "userId")
    private long userId;

    @Id
    @Column(name = "optionId")
    private long optionId;

    public UserPollVoteTable() {
    }

    public UserPollVoteTable(long userId, long optionId) {
        this.userId = userId;
        this.optionId = optionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOptionId() {
        return optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPollVoteTable that = (UserPollVoteTable) o;
        return userId == that.userId && optionId == that.optionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, optionId);
    }
}
