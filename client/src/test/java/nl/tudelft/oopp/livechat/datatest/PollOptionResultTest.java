package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.PollOption;
import nl.tudelft.oopp.livechat.data.PollOptionResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PollOptionResultTest {
    private static PollOptionResult result;
    private static PollOption pollOption;

    @BeforeAll
    static void setUp() {
        pollOption = new PollOption(9764734565634L,753462345653L, "Seagull", 88, true);
        result = new PollOptionResult(pollOption);
    }

    @Test
    public void emptyConstructorTest() {
        PollOptionResult res = new PollOptionResult();
        assertNotNull(res);
    }

    @Test
    public void constructorTest() {
        assertNotNull(result);
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(result, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(result, result);
    }

    @Test
    void equalsEqualTest() {
        PollOption p = new PollOption(9764734565634L,753462345653L, "Seagull", 88, true);
        PollOptionResult res = new PollOptionResult(p);
        assertEquals(result, res);
    }

    @Test
    void equalsDifferentTest() {
        PollOption p = new PollOption(8735563565634L,753462345653L, "Seagull", 88, true);
        PollOptionResult res = new PollOptionResult(p);
        assertNotEquals(result, res);
    }

    @Test
    void equalsDifferentPollIdTest() {
        PollOption p = new PollOption(9764734565634L,454363225653L, "Seagull", 88, true);
        PollOptionResult res = new PollOptionResult(p);
        assertNotEquals(result, res);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(pollOption.getId(), pollOption.getPollId());
        assertEquals(hash, result.hashCode());
    }

    @Test
    void hashCodeEqualsTest() {
        PollOption p = new PollOption(9764734565634L,753462345653L, "Seagull", 88, true);
        PollOptionResult res = new PollOptionResult(p);
        assertEquals(result.hashCode(), res.hashCode());
    }

    @Test
    void hashCodeDifferentTest() {
        PollOption p = new PollOption(8735563565634L,753462345653L, "Seagull", 88, true);
        PollOptionResult res = new PollOptionResult(p);
        assertNotEquals(result.hashCode(), res.hashCode());
    }
}