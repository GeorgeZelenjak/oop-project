package nl.tudelft.oopp.livechat.entities.poll;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PollOptionEntityTest {
    private static PollOptionEntity pollOptionEntity;

    @BeforeAll
    static void setUp() {
        pollOptionEntity = new PollOptionEntity(444, "Seagull", 3, true);
    }

    @Test
    void emptyConstructorTest() {
        PollOptionEntity p = new PollOptionEntity();
        assertNotNull(p);
    }

    @Test
    void constructorTest() {
        assertNotNull(pollOptionEntity);
    }

    @Test
    void getIdTest() {
        assertTrue(pollOptionEntity.getId() >= 0);
    }

    @Test
    void getPollIdTest() {
        assertEquals(444, pollOptionEntity.getPollId());
    }

    @Test
    void setPollIdTest() {
        pollOptionEntity.setPollId(333);
        assertEquals(333, pollOptionEntity.getPollId());

        pollOptionEntity.setPollId(444);
    }

    @Test
    void getOptionTextTest() {
        assertEquals("Seagull", pollOptionEntity.getOptionText());
    }

    @Test
    void setOptionTextTest() {
        pollOptionEntity.setOptionText("Pelican");
        assertEquals("Pelican", pollOptionEntity.getOptionText());

        pollOptionEntity.setOptionText("Seagull");
    }

    @Test
    void getVotesTest() {
        assertEquals(3, pollOptionEntity.getVotes());
    }

    @Test
    void setVotesTest() {
        pollOptionEntity.setVotes(1);
        assertEquals(1, pollOptionEntity.getVotes());

        pollOptionEntity.setVotes(3);
    }

    @Test
    void isCorrectTest() {
        assertTrue(pollOptionEntity.isCorrect());
    }

    @Test
    void setCorrectTest() {
        pollOptionEntity.setCorrect(false);
        assertFalse(pollOptionEntity.isCorrect());

        pollOptionEntity.setCorrect(true);
    }

    @Test
    void testEqualsNullTest() {
        assertNotEquals(pollOptionEntity, null);
    }

    @Test
    void testEqualsSameTest() {
        assertEquals(pollOptionEntity, pollOptionEntity);
    }

    @Test
    void testEqualsDifferentTest() {
        PollOptionEntity p = new PollOptionEntity(444, "Seagull", 3, true);
        assertNotEquals(pollOptionEntity, p);
    }

    @Test
    void testHashCodeTest() {
        int hash = Objects.hash(pollOptionEntity.getId());
        assertEquals(hash, pollOptionEntity.hashCode());
    }

    @Test
    void testHashCodeDifferentTest() {
        PollOptionEntity p = new PollOptionEntity(444, "Seagull", 3, true);
        assertNotEquals(p.hashCode(), pollOptionEntity.hashCode());
    }
}