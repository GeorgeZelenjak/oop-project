package nl.tudelft.oopp.livechat.entities.poll;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "userPollVote")
@IdClass(UserPollVotePairId.class)
public class UserPollVoteTable {
    @Id
    @Column(name = "userId")
    private long userId;

    @Id
    @Column(name = "optionId")
    private long optionId;

    /**
     * Creates a new UserPollVoteTable entity.
     */
    public UserPollVoteTable() {
    }

    /**
     * Creates a new UserPollVoteTable entity.
     * @param userId the id of the user
     * @param optionId the id of the option
     */
    public UserPollVoteTable(long userId, long optionId) {
        this.userId = userId;
        this.optionId = optionId;
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
     * Gets the id of the option.
     * @return the id of the option
     */
    public long getOptionId() {
        return this.optionId;
    }

    /**
     * Sets the id of the option.
     * @param optionId the id of the option
     */
    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    /**
     * Compares the UserPollVoteTable object to another object.
     * @param o the other object to compare to
     * @return true iff the other object is also a UserPollVoteTable
     *         object and has the same user id and option id. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UserPollVoteTable) {
            UserPollVoteTable that = (UserPollVoteTable) o;
            return this.userId == that.userId && this.optionId == that.optionId;
        }
        return false;
    }

    /**
     * Generates the hash code for the UserPollVoteTable object.
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, optionId);
    }
}
