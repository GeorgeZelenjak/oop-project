package nl.tudelft.oopp.livechat.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class PollAndOptions {

    private Poll poll;

    private List<PollOption> options;


    public static PollAndOptions getCurrentPollAndOptions() {
        return currentPollAndOptions;
    }

    public static void setCurrentPollAndOptions(PollAndOptions currentPollAndOptions) {
        PollAndOptions.currentPollAndOptions = currentPollAndOptions;
    }

    private static PollAndOptions currentPollAndOptions;

    public PollAndOptions() {
    }

    public PollAndOptions(Poll poll, List<PollOption> options) {
        this.poll = poll;
        this.options = options;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollAndOptions that = (PollAndOptions) o;
        return Objects.equals(poll, that.poll) && Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(poll, options);
    }
}