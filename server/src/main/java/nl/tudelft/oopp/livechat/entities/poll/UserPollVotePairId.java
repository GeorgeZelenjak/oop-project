package nl.tudelft.oopp.livechat.entities.poll;

import java.io.Serializable;
import java.util.Objects;


public class UserPollVotePairId implements Serializable {
    /**
     * The id of the user.
     */
    private long userId;
    /**
     * The id of the option.
     */
    private long optionId;

    /**
     * Creates a new UserPollVotePairId object.
     */
    public UserPollVotePairId() {
    }

    /**
     * Creates a new UserPollVotePairId object with the specified parameters.
     * @param userId the id of the user
     * @param optionId the id of the option
     */
    public UserPollVotePairId(long userId, long optionId) {
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
     * @param optionId  the id of the option
     */
    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    /**
     * Compares the UserPollVotePairId object to another object.
     * @param o the other object to compare to
     * @return true iff the other object is also a UserPollVotePairId
     *         object and has the same user id and option id. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UserPollVotePairId) {
            UserPollVotePairId that = (UserPollVotePairId) o;
            return this.userId == that.userId && this.optionId == that.optionId;
        }
        return false;
    }

    /**
     * Generates the hash code for the UserPollVotePairId object.
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.optionId);
    }
}
