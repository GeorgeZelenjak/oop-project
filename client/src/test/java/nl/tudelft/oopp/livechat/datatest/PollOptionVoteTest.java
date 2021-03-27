package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.PollOption;
import nl.tudelft.oopp.livechat.data.PollOptionVote;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PollOptionVoteTest {

    private static PollOptionVote vote;
    private static PollOption pollOption;

    @BeforeAll
    static void setUp() {
        pollOption = new PollOption(34546574565L,745553L, "Pelican", 200, false);
        vote = new PollOptionVote(pollOption);
    }

    @Test
    public void emptyConstructorTest() {
        PollOptionVote res = new PollOptionVote();
        assertNotNull(res);
    }

    @Test
    public void constructorTest() {
        assertNotNull(vote);
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(vote, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(vote, vote);
    }

    @Test
    void equalsEqualTest() {
        PollOption p = new PollOption(34546574565L,745553L, "Pelican", 200, false);
        PollOptionVote res = new PollOptionVote(p);
        assertEquals(vote, res);
    }

    @Test
    void equalsDifferentTest() {
        PollOption p = new PollOption(745353565634L,745553L, "Pelican", 200, false);
        PollOptionVote res = new PollOptionVote(p);
        assertNotEquals(vote, res);
    }

    @Test
    void equalsDifferentPollIdTest() {
        PollOption p = new PollOption(34546574565L,5345653L, "Pelican", 200, false);
        PollOptionVote res = new PollOptionVote(p);
        assertNotEquals(vote, res);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(pollOption.getId(), pollOption.getPollId());
        assertEquals(hash, vote.hashCode());
    }

    @Test
    void hashCodeEqualsTest() {
        PollOption p = new PollOption(34546574565L,745553L, "Pelican", 200, false);
        PollOptionVote res = new PollOptionVote(p);
        assertEquals(vote.hashCode(), res.hashCode());
    }

    @Test
    void hashCodeDifferentTest() {
        PollOption p = new PollOption(745353565634L,5345653L, "Pelican", 200, false);
        PollOptionVote res = new PollOptionVote(p);
        assertNotEquals(vote.hashCode(), res.hashCode());
    }
}