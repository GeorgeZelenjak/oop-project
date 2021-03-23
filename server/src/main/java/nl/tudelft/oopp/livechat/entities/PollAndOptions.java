package nl.tudelft.oopp.livechat.entities;

import java.util.List;
import java.util.Objects;

public class PollAndOptions {

    private PollEntity poll;
    private List<PollOptionEntity> options;

    public PollAndOptions() {
    }

    public PollAndOptions(PollEntity poll, List<PollOptionEntity> options) {
        this.poll = poll;
        this.options = options;
    }

    public PollEntity getPoll() {
        return poll;
    }

    public void setPoll(PollEntity poll) {
        this.poll = poll;
    }

    public List<PollOptionEntity> getOptions() {
        return options;
    }

    public void setOptions(List<PollOptionEntity> options) {
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
