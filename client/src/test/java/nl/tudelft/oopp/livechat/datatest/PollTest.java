package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.Poll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PollTest {
    private static Poll poll;
    private static final UUID lid = UUID.randomUUID();
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    @BeforeAll
    static void setUp() {
        poll = new Poll(lid, "What would you do if a pelican entered in your house?",
                time, 42, false);
    }

    @Test
    void emptyConstructorTest() {
        Poll p = new Poll();
        assertNotNull(p);
    }

    @Test
    void fullConstructorTest() {
        assertNotNull(poll);
    }

    @Test
    void partialConstructorTest() {
        Poll p = new Poll(lid, "What would you do if a seagull entered in your house?");
        assertNotNull(p);
    }

    @Test
    void getIdTest() {
        assertTrue(poll.getId() >= 0);
    }

    @Test
    void getLectureIdTest() {
        assertEquals(lid, poll.getLectureId());
    }

    @Test
    void setLectureIdTest() {
        UUID newLid = UUID.randomUUID();
        poll.setLectureId(newLid);
        assertEquals(newLid, poll.getLectureId());

        poll.setLectureId(lid);
    }

    @Test
    void getQuestionTextTest() {
        assertEquals("What would you do if a pelican entered in your house?",
                poll.getQuestionText());
    }

    @Test
    void setQuestionTextTest() {
        poll.setQuestionText("What would you do if a seagull entered in your house?");
        assertEquals("What would you do if a seagull entered in your house?",
                poll.getQuestionText());

        poll.setQuestionText("What would you do if a pelican entered in your house?");
    }

    @Test
    void getTimeTest() {
        assertEquals(time, poll.getTime());
    }

    @Test
    void setTimeTest() {
        Timestamp newTime = new Timestamp(System.currentTimeMillis() - 2000L);
        poll.setTime(newTime);
        assertEquals(newTime, poll.getTime());

        poll.setTime(time);
    }

    @Test
    void getVotesTest() {
        assertEquals(42, poll.getVotes());
    }

    @Test
    void setVotesTest() {
        poll.setVotes(43);
        assertEquals(43, poll.getVotes());

        poll.setVotes(42);
    }

    @Test
    void isOpenTest() {
        assertFalse(poll.isOpen());
    }

    @Test
    void setOpenTest() {
        poll.setOpen(true);
        assertTrue(poll.isOpen());

        poll.setOpen(false);
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(poll, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(poll, poll);
    }

    @Test
    void equalsDifferentTest() {
        Poll p = new Poll(lid, "What would you do if a pelican entered in your house?",
                time, 42, false);
        assertNotEquals(poll, p);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(poll.getId(), poll.getQuestionText());
        assertEquals(hash, poll.hashCode());
    }

    @Test
    void hashCodeDifferentTest() {
        Poll p = new Poll(lid, "What would you do if a pelican entered in your house?",
                time, 42, false);
        assertNotEquals(p.hashCode(), poll.hashCode());
    }
}
