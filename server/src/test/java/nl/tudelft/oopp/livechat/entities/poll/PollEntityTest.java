package nl.tudelft.oopp.livechat.entities.poll;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PollEntityTest {
    private static PollEntity pollEntity;
    private static final UUID lid = UUID.randomUUID();
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    @BeforeAll
    static void setUp() {
        pollEntity = new PollEntity(lid, "What would you do if a pelican entered in your house?",
                time, 13, true);
    }

    @Test
    void emptyConstructorTest() {
        PollEntity p = new PollEntity();
        assertNotNull(p);
    }

    @Test
    void fullConstructorTest() {
        assertNotNull(pollEntity);
    }

    @Test
    void partialConstructorTest() {
        PollEntity p = new PollEntity(lid, "What would you do if a seagull entered in your house?");
        assertNotNull(p);
    }

    @Test
    void getIdTest() {
        assertTrue(pollEntity.getId() >= 0);
    }

    @Test
    void getLectureIdTest() {
        assertEquals(lid, pollEntity.getLectureId());
    }

    @Test
    void setLectureIdTest() {
        UUID newLid = UUID.randomUUID();
        pollEntity.setLectureId(newLid);
        assertEquals(newLid, pollEntity.getLectureId());

        pollEntity.setLectureId(lid);
    }

    @Test
    void getQuestionTextTest() {
        assertEquals("What would you do if a pelican entered in your house?",
                pollEntity.getQuestionText());
    }

    @Test
    void setQuestionTextTest() {
        pollEntity.setQuestionText("What would you do if a seagull entered in your house?");
        assertEquals("What would you do if a seagull entered in your house?",
                pollEntity.getQuestionText());

        pollEntity.setQuestionText("What would you do if a pelican entered in your house?");
    }

    @Test
    void getTimeTest() {
        assertEquals(time, pollEntity.getTime());
    }

    @Test
    void setTimeTest() {
        Timestamp newTime = new Timestamp(System.currentTimeMillis() - 1000L);
        pollEntity.setTime(newTime);
        assertEquals(newTime, pollEntity.getTime());

        pollEntity.setTime(time);
    }

    @Test
    void getVotesTest() {
        assertEquals(13, pollEntity.getVotes());
    }

    @Test
    void setVotesTest() {
        pollEntity.setVotes(22);
        assertEquals(22, pollEntity.getVotes());

        pollEntity.setVotes(13);
    }

    @Test
    void isOpenTest() {
        assertTrue(pollEntity.isOpen());
    }

    @Test
    void setOpenTest() {
        pollEntity.setOpen(false);
        assertFalse(pollEntity.isOpen());

        pollEntity.setOpen(true);
    }

    @Test
    void testEqualsNullTest() {
        assertNotEquals(pollEntity, null);
    }

    @Test
    void testEqualsSameTest() {
        assertEquals(pollEntity, pollEntity);
    }

    @Test
    void testEqualsDifferentTest() {
        PollEntity p = new PollEntity(lid, "What would you do if a pelican entered in your house?",
                time, 13, true);
        assertNotEquals(pollEntity, p);
    }

    @Test
    void testHashCodeTest() {
        int hash = Objects.hash(pollEntity.getId());
        assertEquals(hash, pollEntity.hashCode());
    }

    @Test
    void testHashCodeDifferentTest() {
        PollEntity p = new PollEntity(lid, "What would you do if a pelican entered in your house?",
                time, 13, true);
        assertNotEquals(p.hashCode(), pollEntity.hashCode());
    }
}