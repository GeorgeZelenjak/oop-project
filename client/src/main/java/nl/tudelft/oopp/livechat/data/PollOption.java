package nl.tudelft.oopp.livechat.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class PollOption implements Comparable<PollOption> {

    private static List<PollOption> currentPollOptions;
    private static PollOption currentPollOption;

    private  long id;

    private long pollId;

    private String optionText;

    private long votes;

    private boolean isCorrect;

    /**
     * Instantiates a new Poll option entity.
     */
    public PollOption() {
    }

    /**
     * Instantiates a new Poll option entity.
     *
     * @param pollId     the poll id
     * @param optionText the option text
     * @param votes      the votes
     * @param isCorrect  true if it's a correct option, false if not quiz or not the answer
     */
    public PollOption(long pollId, String optionText, long votes, boolean isCorrect) {
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
        PollOption that = (PollOption) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pollId, optionText, votes, isCorrect);
    }

    /**
     * Compares the current poll option to another poll option.
     * @param other the poll option to compare to
     * @return -1 if current poll option was asked later
     *          0 if both poll option were asked at the same time
     *          1 if current poll option was asked earlier
     */
    @Override
    public int compareTo(PollOption other) {
        return Long.compare(this.getId(), other.getId());
    }

    public static List<PollOption> getCurrentPollOptions() {
        return currentPollOptions;
    }

    public static void setCurrentPollOptions(List<PollOption> currentPollOptions) {
        PollOption.currentPollOptions = currentPollOptions;
    }

    public static PollOption getCurrentPollOption() {
        return currentPollOption;
    }

    public static void setCurrentPollOption(PollOption currentPollOption) {
        PollOption.currentPollOption = currentPollOption;
    }
}
