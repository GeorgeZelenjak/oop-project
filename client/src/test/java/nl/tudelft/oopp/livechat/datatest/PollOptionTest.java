package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.PollOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class PollOptionTest {
    private static PollOption pollOption;
    private static PollOption option;

    @BeforeAll
    static void setUp() {
        pollOption = new PollOption(22,1024, "Seagull", 17, false);
        option = new PollOption(44, 2048, "Pelican", 3, true);
        PollOption.setCurrentPollOption(pollOption);
        PollOption.setCurrentPollOptions(List.of(pollOption, option));
    }

    @Test
    void emptyConstructorTest() {
        PollOption p = new PollOption();
        assertNotNull(p);
    }

    @Test
    void partialConstructorTest() {
        PollOption p = new PollOption(404, "404", 404, false);
        assertNotNull(p);
    }

    @Test
    void constructorTest() {
        assertNotNull(pollOption);
    }

    @Test
    void getIdTest() {
        assertTrue(pollOption.getId() >= 0);
    }

    @Test
    void getPollIdTest() {
        assertEquals(1024, pollOption.getPollId());
    }

    @Test
    void setPollIdTest() {
        pollOption.setPollId(512);
        assertEquals(512, pollOption.getPollId());

        pollOption.setPollId(1024);
    }

    @Test
    void getOptionTextTest() {
        assertEquals("Seagull", pollOption.getOptionText());
    }

    @Test
    void setOptionTextTest() {
        pollOption.setOptionText("Pelican");
        assertEquals("Pelican", pollOption.getOptionText());

        pollOption.setOptionText("Seagull");
    }

    @Test
    void getVotesTest() {
        assertEquals(17, pollOption.getVotes());
    }

    @Test
    void setVotesTest() {
        pollOption.setVotes(2);
        assertEquals(2, pollOption.getVotes());

        pollOption.setVotes(17);
    }

    @Test
    void isCorrectTest() {
        assertFalse(pollOption.isCorrect());
    }

    @Test
    void setCorrectTest() {
        pollOption.setCorrect(true);
        assertTrue(pollOption.isCorrect());

        pollOption.setCorrect(false);
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(pollOption, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(pollOption, pollOption);
    }

    @Test
    void equalsEqualTest() {
        PollOption p = new PollOption(22,1024, "Seagull", 17, false);
        assertEquals(pollOption, p);
    }

    @Test
    void equalsDifferentTest() {
        assertNotEquals(pollOption, option);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(pollOption.getId(), pollOption.getPollId(),
                pollOption.getOptionText());
        assertEquals(hash, pollOption.hashCode());
    }

    @Test
    void hashCodeEqualsTest() {
        PollOption o = new PollOption(22,1024, "Seagull", 17, false);
        assertEquals(o.hashCode(), pollOption.hashCode());
    }

    @Test
    void hashCodeDifferentTest() {
        assertNotEquals(option.hashCode(), pollOption.hashCode());
    }

    @Test
    void getCurrentPollOptionTest() {
        assertEquals(pollOption, PollOption.getCurrentPollOption());
    }

    @Test
    void setCurrentPollOptionTest() {
        PollOption.setCurrentPollOption(option);
        assertEquals(option, PollOption.getCurrentPollOption());

        PollOption.setCurrentPollOption(pollOption);
    }

    @Test
    void getCurrentPollOptionsTest() {
        assertEquals(List.of(pollOption, option), PollOption.getCurrentPollOptions());
    }

    @Test
    void setCurrentPollOptionsTest() {
        PollOption o1 = new PollOption(1445,5436, "Pipeline", 543, false);
        PollOption o2 = new PollOption(7654,9876, "Commit", 342, false);
        List<PollOption> options = List.of(o1, o2);
        PollOption.setCurrentPollOptions(options);
        assertEquals(options, PollOption.getCurrentPollOptions());

        PollOption.setCurrentPollOptions(List.of(pollOption, option));
    }

}
