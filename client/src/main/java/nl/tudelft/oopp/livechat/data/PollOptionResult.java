package nl.tudelft.oopp.livechat.data;

public class PollOptionResult extends PollOption {

    public PollOptionResult() {
    }

    public PollOptionResult(PollOption option) {
        super(option.getId(), option.getPollId(), option.getOptionText(),
                option.getVotes(), option.isCorrect());
    }
}
