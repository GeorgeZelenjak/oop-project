package nl.tudelft.oopp.livechat.data;

public class PollOptionVote extends PollOption {

    /**
     * Creates a new vote for a poll option.
     */
    public PollOptionVote() {
    }

    /**
     * Creates a new poll option vote for the the specified poll option.
     * @param option the corresponding poll option
     */
    public PollOptionVote(PollOption option) {
        super(option.getId(), option.getPollId(), option.getOptionText(),
                option.getVotes(), option.isCorrect());
    }

    /**
     * Compares the PollOptionVote object to another object.
     * @param other the other object to compare to
     * @return true iff the other object is also a PollOptionVote
     *         object and has equal parent class. False otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof PollOptionVote) {
            PollOptionVote that = (PollOptionVote) other;
            return super.equals(that);
        }
        return false;
    }

    /**
     * Generates the hash code for the PollOptionVote object.
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
