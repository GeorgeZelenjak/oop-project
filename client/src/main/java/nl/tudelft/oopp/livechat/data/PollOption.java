package nl.tudelft.oopp.livechat.data;

import java.util.List;
import java.util.Objects;

public class PollOption {

    private static List<PollOption> currentPollOptions;
    private static PollOption currentPollOption;

    private long id;

    private long pollId;

    private String optionText = "";

    private long votes;

    private boolean isCorrect;

    /**
     * Creates a new PollOption object.
     */
    public PollOption() {
    }

    /**
     * Creates a new PollOptionEntity object with the specified parameters.
     * @param id the id of the option
     * @param pollId the id of the poll
     * @param optionText the text of the option
     * @param votes the number of votes for the option
     * @param isCorrect true if it is a correct option
     */
    public PollOption(long id, long pollId, String optionText, long votes, boolean isCorrect) {
        this.id = id;
        this.pollId = pollId;
        this.optionText = optionText;
        this.votes = votes;
        this.isCorrect = isCorrect;
    }

    /**
     * Creates a new PollOptionEntity object with the specified parameters.
     * @param pollId the id of the poll
     * @param optionText the text of the option
     * @param votes the number of votes for the option
     * @param isCorrect true if it is a correct option
     */
    public PollOption(long pollId, String optionText, long votes, boolean isCorrect) {
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
     * Compares the PollOption object to another object.
     * @param o the other object to compare to
     * @return true iff the other object is also a PollOption
     *         object and has the same id and text. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PollOption) {
            PollOption that = (PollOption) o;
            //IT IS VERY IMPORTANT TO CHECK THE TEXT ON CLIENT
            //TODO why is that?
            return (this.id == that.id && this.pollId == that.pollId);
        }
        return false;
    }

    /**
     * Generates the hash code for the PollOption object.
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, pollId, optionText);
    }


    /**
     * Gets the list of current poll options for the current poll.
     * @return the list of current poll options for the current poll
     */
    public static List<PollOption> getCurrentPollOptions() {
        return currentPollOptions;
    }

    /**
     * Sets the list of current poll options for the current poll.
     * @param currentPollOptions the list of current poll options for the current poll
     */
    public static void setCurrentPollOptions(List<PollOption> currentPollOptions) {
        PollOption.currentPollOptions = currentPollOptions;
    }

    /**
     * Gets the current poll option.
     * @return the current poll option
     */
    public static PollOption getCurrentPollOption() {
        return currentPollOption;
    }

    /**
     * Sets the current poll option.
     * @param currentPollOption the current poll option
     */
    public static void setCurrentPollOption(PollOption currentPollOption) {
        PollOption.currentPollOption = currentPollOption;
    }
}
