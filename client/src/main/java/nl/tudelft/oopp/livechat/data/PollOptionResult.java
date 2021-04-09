package nl.tudelft.oopp.livechat.data;

public class PollOptionResult extends PollOption {

    /**
     * Creates a new poll option result.
     */
    public PollOptionResult() {
    }

    /**
     * Creates a new poll option result for the the specified poll option.
     * @param option the corresponding poll option
     */
    public PollOptionResult(PollOption option) {
        super(option.getId(), option.getPollId(), option.getOptionText(),
                option.getVotes(), option.isCorrect());
    }

    /**
     * Compares the PollOptionResult object to another object.
     * @param other the other object to compare to
     * @return true iff the other object is also a PollOptionResult
     *         object and has equal parent class. False otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof PollOptionResult) {
            PollOptionResult that = (PollOptionResult) other;
            return super.equals(that);
        }
        return false;
    }

    /**
     * Generates the hash code for the PollOptionResult object.
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
