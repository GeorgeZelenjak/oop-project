package nl.tudelft.oopp.livechat.entities.poll;

import java.io.Serializable;
import java.util.Objects;

public class UserPollVotePairId implements Serializable {

    private long userId;
    private long optionId;

    public UserPollVotePairId() {
    }

    public UserPollVotePairId(long userId, long optionId) {
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
        UserPollVotePairId that = (UserPollVotePairId) o;
        return userId == that.userId && optionId == that.optionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, optionId);
    }
}
