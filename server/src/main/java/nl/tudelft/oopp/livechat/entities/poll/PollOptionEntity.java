package nl.tudelft.oopp.livechat.entities.poll;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "pollOptions")
public class PollOptionEntity {
    @Id
    @Column(name = "id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "id")
    private final long id  = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

    @Column(name = "pollId")
    private long pollId;

    @Column(name = "optionText")
    private String optionText;

    @Column(name = "votes")
    private long votes;

    @Column(name = "isCorrect")
    private boolean isCorrect;

    /**
     * Creates a new PollOptionEntity object.
     */
    public PollOptionEntity() {
    }

    /**
     * Creates a new PollOptionEntity object with the specified parameters.
     * @param pollId the id of the poll
     * @param optionText the text of the option
     * @param votes the number of votes for the option
     * @param isCorrect true if it is a correct option
     */
    public PollOptionEntity(long pollId, String optionText, long votes, boolean isCorrect) {
        this.pollId = pollId;
        this.optionText = optionText;
        this.votes = votes;
        this.isCorrect = isCorrect;
    }


    /**
     * Gets the id of the poll option.
     * @return the id of the poll option
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the id of the poll.
     * @return the id of the poll
     */
    public long getPollId() {
        return pollId;
    }

    /**
     * Sets the id of the poll.
     * @param pollId the new id of the poll
     */
    public void setPollId(long pollId) {
        this.pollId = pollId;
    }

    /**
     * Gets the text of the option.
     * @return the text of the option
     */
    public String getOptionText() {
        return optionText;
    }

    /**
     * Sets the text of the option.
     * @param optionText the new text of the option
     */
    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    /**
     * Gets the number of votes for the option.
     * @return the number of votes for the option
     */
    public long getVotes() {
        return votes;
    }

    /**
     * Sets the number of votes for the option.
     * @param votes the new number of votes for the option
     */
    public void setVotes(long votes) {
        this.votes = votes;
    }

    /**
     * Checks if it is a correct option.
     * @return true if it is a correct option
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    /**
     * Sets the option as correct or incorrect.
     * @param correct true if correct, false if not
     */
    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    /**
     * Compares the PollOptionEntity object to another object.
     * @param o the other object to compare to
     * @return true iff the other object is also a PollOptionEntity
     *         object and has the same id. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PollOptionEntity) {
            PollOptionEntity that = (PollOptionEntity) o;
            return id == that.id;
        }
        return false;
    }

    /**
     * Generates the hash code for the PollOptionEntity object.
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
