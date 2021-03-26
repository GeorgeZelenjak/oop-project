package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.PollOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

//TODO finish (include static methods)
public class PollOptionTest {
    private static PollOption pollOption;

    @BeforeAll
    static void setUp() {
        pollOption = new PollOption(22,1024, "Seagull", 17, false);
    }

    @Test
    void emptyConstructorTest() {
        PollOption p = new PollOption();
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
    void testEqualsNullTest() {
        assertNotEquals(pollOption, null);
    }

    @Test
    void testEqualsSameTest() {
        assertEquals(pollOption, pollOption);
    }

    @Test
    void testEqualsDifferentTest() {
        PollOption p = new PollOption(123,1024, "Seagull", 17, false);
        assertNotEquals(pollOption, p);
    }

    @Test
    void testHashCodeTest() {
        int hash = Objects.hash(pollOption.getId(), pollOption.getPollId(),
                pollOption.getOptionText());
        assertEquals(hash, pollOption.hashCode());
    }

    @Test
    void testHashCodeDifferentTest() {
        PollOption p = new PollOption(1024, "Seagull", 17, false);
        assertNotEquals(p.hashCode(), pollOption.hashCode());
    }
}
