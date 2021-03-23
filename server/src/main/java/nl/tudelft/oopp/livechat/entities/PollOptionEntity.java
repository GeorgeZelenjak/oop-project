package nl.tudelft.oopp.livechat.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The type Poll option entity.
 */
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
     * Instantiates a new Poll option entity.
     */
    public PollOptionEntity() {
    }

    /**
     * Instantiates a new Poll option entity.
     *
     * @param pollId     the poll id
     * @param optionText the option text
     * @param votes      the votes
     * @param isCorrect  true if it's a correct option, false if not quiz or not the answer
     */
    public PollOptionEntity(long pollId, String optionText, long votes, boolean isCorrect) {
        this.pollId = pollId;
        this.optionText = optionText;
        this.votes = votes;
        this.isCorrect = isCorrect;
    }



    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets poll id.
     *
     * @return the poll id
     */
    public long getPollId() {
        return pollId;
    }

    /**
     * Sets poll id.
     *
     * @param pollId the poll id
     */
    public void setPollId(long pollId) {
        this.pollId = pollId;
    }

    /**
     * Gets option text.
     *
     * @return the option text
     */
    public String getOptionText() {
        return optionText;
    }

    /**
     * Sets option text.
     *
     * @param optionText the option text
     */
    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    /**
     * Gets votes.
     *
     * @return the votes
     */
    public long getVotes() {
        return votes;
    }

    /**
     * Sets votes.
     *
     * @param votes the votes
     */
    public void setVotes(long votes) {
        this.votes = votes;
    }

    /**
     * Is correct boolean.
     *
     * @return the boolean
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    /**
     * Sets correct.
     *
     * @param correct the correct
     */
    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollOptionEntity that = (PollOptionEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pollId, optionText, votes, isCorrect);
    }
}
