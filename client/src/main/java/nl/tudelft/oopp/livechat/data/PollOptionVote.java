package nl.tudelft.oopp.livechat.data;

public class PollOptionVote extends PollOption {

    public PollOptionVote() {
    }

    public PollOptionVote(PollOption option) {
        super(option.getId(), option.getPollId(), option.getOptionText(),
                option.getVotes(), option.isCorrect());
    }
}
