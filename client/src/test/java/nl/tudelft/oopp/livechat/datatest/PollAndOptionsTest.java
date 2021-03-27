package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.Poll;
import nl.tudelft.oopp.livechat.data.PollAndOptions;
import nl.tudelft.oopp.livechat.data.PollOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PollAndOptionsTest {
    private static Poll poll;
    private static PollOption option1;
    private static PollOption option2;
    private static PollAndOptions pollAndOptions;
    private static final UUID lid = UUID.randomUUID();
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    @BeforeAll
    static void setUp() {
        poll = new Poll(lid, "What would you do if a pelican entered in your house?",
                time, 33, false);
        option1 = new PollOption(poll.getId(), "Kill it", 11, true);
        option2 = new PollOption(poll.getId(), "Eat it", 27, false);
        pollAndOptions = new PollAndOptions(poll, List.of(option1, option2));
        PollAndOptions.setCurrentPollAndOptions(pollAndOptions);
    }

    @Test
    void emptyConstructorTest() {
        PollAndOptions p = new PollAndOptions();
        assertNotNull(p);
    }

    @Test
    void constructorTest() {
        assertNotNull(pollAndOptions);
    }

    @Test
    void getPollTest() {
        assertEquals(poll, pollAndOptions.getPoll());
    }

    @Test
    void setPollTest() {
        Poll p = new Poll(lid, "What would you do if a seagull entered in your house?",
                time, 11, true);
        pollAndOptions.setPoll(p);
        assertEquals(p, pollAndOptions.getPoll());

        pollAndOptions.setPoll(poll);
    }

    @Test
    void getOptionsTest() {
        assertEquals(List.of(option1, option2), pollAndOptions.getOptions());
    }

    @Test
    void setOptionsTest() {
        PollOption o1 = new PollOption(poll.getId(), "Fry it", 5, true);
        PollOption o2 = new PollOption(poll.getId(), "Bake it", 9, false);
        List<PollOption> options = List.of(o1, o2);
        pollAndOptions.setOptions(options);
        assertEquals(options, pollAndOptions.getOptions());

        pollAndOptions.setOptions(List.of(option1, option2));
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(pollAndOptions, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(pollAndOptions, pollAndOptions);
    }

    @Test
    void equalsEqualTest() {
        PollAndOptions po = new PollAndOptions(poll, List.of(option1, option2));
        assertEquals(pollAndOptions, po);
    }

    @Test
    void equalsDifferentOptionsTest() {
        PollOption o1 = new PollOption(90, poll.getId(), "Kill it", 11, true);
        PollOption o2 = new PollOption(99, poll.getId(), "Eat it", 27, false);
        PollAndOptions po = new PollAndOptions(poll, List.of(o1, o2));
        assertNotEquals(pollAndOptions, po);
    }

    @Test
    void equalsDifferentPollTest() {
        Poll p = new Poll(lid, "What would you do if a pelican entered in your house?",
                time, 33, false);
        PollAndOptions po = new PollAndOptions(p, List.of(option1, option2));
        assertNotEquals(pollAndOptions, po);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(pollAndOptions.getPoll(), pollAndOptions.getOptions());
        assertEquals(hash, pollAndOptions.hashCode());
    }

    @Test
    void hashCodeEqualsTest() {
        PollAndOptions po = new PollAndOptions(pollAndOptions.getPoll(),
                pollAndOptions.getOptions());
        assertEquals(po.hashCode(), pollAndOptions.hashCode());
    }

    @Test
    void hashCodeDifferentTest() {
        Poll p = new Poll(lid, "What would you do if a pelican entered in your house?",
                time, 33, false);
        PollOption o1 = new PollOption(p.getId(), "Kill it", 11, true);
        PollOption o2 = new PollOption(p.getId(), "Eat it", 27, false);
        PollAndOptions po = new PollAndOptions(p, List.of(o1, o2));
        assertNotEquals(po.hashCode(), pollAndOptions.hashCode());
    }

    @Test
    void getCurrentPollAndOptionsTest() {
        assertEquals(pollAndOptions, PollAndOptions.getCurrentPollAndOptions());
    }

    @Test
    void setCurrentPollAndOptionsTest() {
        Poll p = new Poll(lid, "What would you do if a seagull entered in your house?",
                time, 33, false);
        PollOption o1 = new PollOption(p.getId(), "Kill it", 11, true);
        PollOption o2 = new PollOption(p.getId(), "Eat it", 27, false);
        PollAndOptions po = new PollAndOptions(p, List.of(o1, o2));
        PollAndOptions.setCurrentPollAndOptions(po);
        assertEquals(po, PollAndOptions.getCurrentPollAndOptions());

        PollAndOptions.setCurrentPollAndOptions(pollAndOptions);
    }
}
